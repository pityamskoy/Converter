package team.anonyms.converter.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.anonyms.converter.errors.UnsupportedExtensionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public final class ConversionService {
    // Project directory on the server for deployment
    private static final String PROJECT_DIRECTORY = "/root/projects/converter/";

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
     * <p>
     *     Converts JSON file to CSV file. Any other extensions are not supported.<br>
     *     <b>Assumption</b>: all nested objects in JSON file will be written to CSV file as strings.
     * </p>
     * @param jsonFile JSON file written in {@link MultipartFile} instance.
     * @return path to converted CSV file.
     * @throws IllegalArgumentException if either {@code jsonFile} is empty or it consists of
     * unsupported structure for conversion from JSON to CSV.
     * @throws NullPointerException if filename is null.
     * @throws UnsupportedExtensionException if a file without '.json' extension was provided.
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

        return csvPath;
    }

    /**
     * <p>
     *     Converts CSV file to JSON file. Any other extensions are not supported.
     * </p>
     * @param csvFile CSV file written in {@link MultipartFile} instance.
     * @return path to converted CSV file.
     * @throws IllegalArgumentException if either {@code csvFile} is empty or it consists of
     * unsupported structure for conversion from CSV to JSON.
     * @throws NullPointerException if filename is null.
     * @throws UnsupportedExtensionException if a file without '.csv' extension was provided.
     */
    //fix separator problem
    public @NonNull Path convertCsvFileToJson(@NonNull MultipartFile csvFile) throws IOException {
        // Check and validate csvFile
        if (csvFile.isEmpty()) {
            throw new IllegalArgumentException("csvFile is empty");
        }

        String filename = csvFile.getOriginalFilename();
        if (filename == null) {
            throw new NullPointerException("filename is null");
        }

        if (!filename.endsWith(".csv")) {
            throw new UnsupportedExtensionException("Provided file doesn't have '.csv' extension");
        }

        // Create temporarily JSON file for writing converted data
        String filenameWithoutExtension = getFilenameWithoutExtension(csvFile, ".csv");
        Path jsonPath = Files.createTempFile(filenameWithoutExtension, ".json");

        // Start converting
        ObjectMapper objectMapper = new ObjectMapper();
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();

        List<Map<String, Object>> rows = new ArrayList<>();
        MappingIterator<Map<String, String>> iterator = csvMapper.readerFor(Map.class).
                with(csvSchema).readValues(csvFile.getInputStream());

        while (iterator.hasNext()) {
            Map<String, String> row = iterator.next();
            Map<String, Object> convertedRow = new LinkedHashMap<>();

            for (Map.Entry<String, String> entry : row.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value == null || value.isEmpty()) {
                    convertedRow.put(key, null);
                    continue;
                }

                try {
                    if (value.startsWith("{") || value.startsWith("[")) {
                        JsonNode node = objectMapper.readTree(value);
                        if (node.isObject()) {
                            convertedRow.put(key, objectMapper.convertValue(node, Map.class));
                        } else if (node.isArray()) {
                            convertedRow.put(key, objectMapper.convertValue(node, List.class));
                        }
                    } else if (value.matches("-?\\d+")) {
                        convertedRow.put(key, Long.parseLong(value));
                    } else if (value.matches("-?\\d*\\.\\d+")) {
                        convertedRow.put(key, Double.parseDouble(value));
                    } else {
                        convertedRow.put(key, value);
                    }
                } catch (JsonProcessingException | NumberFormatException e) {
                    convertedRow.put(key, value);
                }
            }

            rows.add(convertedRow);
        }

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("CSV contains no rows to convert");
        }

        if (rows.size() == 1) {
            objectMapper.writeValue(jsonPath.toFile(), rows.getFirst());
        } else {
            objectMapper.writeValue(jsonPath.toFile(), rows);
        }

        return jsonPath;
    }

    /**
     * <p>
     *     Converts JSON file to XML file. Any other extensions are not supported.
     * </p>
     * @param jsonFile JSON file written in {@link MultipartFile} instance.
     * @return path to converted XML file.
     * @throws IllegalArgumentException if {@code jsonFile} is empty.
     * @throws NullPointerException if filename is null.
     * @throws UnsupportedExtensionException if a file without '.json' extension was provided.
     */
    @SuppressWarnings(value = {"unchecked"})
    public @NonNull Path convertJsonFileToXml(@NonNull MultipartFile jsonFile) throws IOException {
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

        // Create temporarily XML file for writing converted data
        String filenameWithoutExtension = getFilenameWithoutExtension(jsonFile, ".json");
        Path xmlPath = Files.createTempFile(filenameWithoutExtension, ".xml");

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

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(xmlPath.toFile(), rows);

        return xmlPath;
    }

}
