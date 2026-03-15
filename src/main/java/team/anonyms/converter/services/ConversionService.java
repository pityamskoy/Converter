package team.anonyms.converter.services;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.DispatcherServlet;
import team.anonyms.converter.annotations.LastSupportedProjectVersion;
import team.anonyms.converter.errors.UnsupportedExtensionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static team.anonyms.converter.enums.ProjectVersion.RELEASE_1;

@Service
public final class ConversionService {
    //
    private static final String DEFAULT_PROJECT_DIRECTORY = "/root/projects/converter/";

    /**
     * <p>
     *     Counts number of occurrences for provided string and substring.
     * </p>
     * @param string main string.
     * @param substring substring.
     * @return number of occurrences.
     */
    public static int countNumberOfOccurrences(@NonNull String string, @NonNull String substring) {
        int count = 0;
        int index = 0;

        while ((index = string.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }

        return count;
    }

    /**
     * <p>
     *     Creates unique path in filesystem. Usage of this method is necessary in multithreading environment.
     * </p>
     * <p>
     *     For example, when several users in the short period of time send files with the same name,
     *     {@link DispatcherServlet} for the last supported version will create multiple threads
     *     to handle these requests, after that {@link FileAlreadyExistsException} may be thrown
     *     in the case of creating file with user's filename without using this method.
     * </p>
     * @param filename filename with extension.
     * @return unique path.
     */
    @LastSupportedProjectVersion(RELEASE_1)
    private @NonNull Path createUniquePathByFilename(@NonNull String filename) {
        String uniqueFilename = DEFAULT_PROJECT_DIRECTORY + filename;

        if (Files.notExists(Path.of(uniqueFilename))) {
            return Path.of(uniqueFilename);
        }

        int count = 1;
        uniqueFilename = "%s%s(%s)".formatted(DEFAULT_PROJECT_DIRECTORY, filename, Integer.toString(count));

        while (Files.exists(Path.of(uniqueFilename))) {
            count++;
            uniqueFilename = "%s%s(%s)".formatted(DEFAULT_PROJECT_DIRECTORY, filename, Integer.toString(count));
        }

        return Path.of(uniqueFilename);
    }

    /**
     * @param file any {@link MultipartFile}.
     * @param currentExtension current extension of {@code file}. It is supposed to have "." in itself. For example,
     *                         ".json" or ".csv" are valid values for {@code currentExtension}, and "json", "csv" are
     *                         not valid.
     *
     * @return filename without provided {@code currentExtension}.
     *
     * @throws IllegalArgumentException if {@code currentExtension} doesn't contain the only "." at the beginning
     * of itself.
     * @throws NullPointerException if the filename of {@code file} is null.
     * @throws UnsupportedExtensionException if {@code currentExtension} is not extension of the {@code file}.
     */
    private @NonNull String getFilenameWithoutExtension(@NonNull MultipartFile file, @NonNull String currentExtension) {
        if (!currentExtension.startsWith(".")) {
            throw new IllegalArgumentException("currentExtension doesn't start with '.' symbol; currentExtension="
                    + currentExtension);
        }

        if (countNumberOfOccurrences(currentExtension, ".") > 1) {
            throw new IllegalArgumentException("currentExtension has more than one dot; currentExtension="
                    + currentExtension);
        }

        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new NullPointerException("filename is null");
        }

        if (!filename.endsWith(currentExtension)) {
            throw new UnsupportedExtensionException("Unsupported extension was provided; currentExtension="
                    + currentExtension);
        }

        return filename.substring(0, filename.length() - currentExtension.length()) ;
    }

    /**
     *<p>
     *     Converts JSON file to CSV file. Any other extensions are not supported.<br>
     *     <b>Assumption</b>: all nested objects in JSON file will be written to CSV file as strings.
     *</p>
     * @param jsonFile JSON file written in {@link MultipartFile} instance.
     * @return path to converted CSV file.
     * @throws IllegalArgumentException if either {@code jsonFile} is empty or it consists of
     * unsupported structure for conversion from JSON to CSV.
     */
    @SuppressWarnings(value = {"unchecked"})
    public @NonNull Path convertJsonFileToCsv(@NonNull MultipartFile jsonFile) throws IOException {
        // Check and validate jsonFile
        if (jsonFile.isEmpty()) {
            throw new IllegalArgumentException("jsonFile is empty");
        }

        String filename = jsonFile.getOriginalFilename();
        if (filename == null) {
            throw new NullPointerException("filename is null");
        }

        if (!filename.endsWith(".json")) {
            throw new UnsupportedExtensionException("Provided file doesn't have '.json' extension");
        }

        // Possible vulnerability here
        // Create temporarily CSV file for writing converted data
        String filenameWithoutExtension = getFilenameWithoutExtension(jsonFile, ".json");
        Path csvPath = Files.createTempFile(filenameWithoutExtension, ".csv");

        // Start converting
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String,Object>> rows = new ArrayList<>();
        JsonNode root = objectMapper.readTree(jsonFile.getInputStream());

        if (root.isArray()) {
            for (JsonNode node : root) {
                rows.add(objectMapper.convertValue(node, Map.class));
            }
        } else if (root.isObject()) {
            rows.add(objectMapper.convertValue(root, Map.class));
        } else {
            throw new IllegalArgumentException("Unsupported JSON structure for CSV conversion");
        }

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("JSON contains no rows to convert");
        }

        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        for (String column : rows.getFirst().keySet()) {
            csvSchemaBuilder.addColumn(column);
        }

        // Converting nested structures to JSON strings
        for (Map<String, Object> row : rows) {
            if (row == null) {
                continue;
            }

            for (Map.Entry<String, Object> entry : new ArrayList<>(row.entrySet())) {
                Object value = entry.getValue();

                if (value instanceof Map || value instanceof Collection) {
                    entry.setValue(objectMapper.writeValueAsString(value));
                } else if (value == null) {
                    entry.setValue("");
                }
            }
        }

        // Writing converted data
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.writerFor(List.class).with(csvSchema).writeValue(csvPath.toFile(), rows);

        // Preparing output file by making it has the original filename
        Path uniquePathWithOriginalFilename = createUniquePathByFilename(filename + ".csv");
        Files.createFile(uniquePathWithOriginalFilename);

        Files.copy(csvPath, uniquePathWithOriginalFilename, StandardCopyOption.REPLACE_EXISTING);
        Files.delete(csvPath);

        return uniquePathWithOriginalFilename;
    }
}
