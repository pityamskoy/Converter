package team.anonyms.converter.services;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.anonyms.converter.errors.UnsupportedExtensionException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.FileAlreadyExistsException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public final class ConversionService {
    private final static Logger log = LoggerFactory.getLogger(ConversionService.class);

    // The project directory path on the server, where the project has been deployed.
    private static final Path PROJECT_DIRECTORY_PATH = Path.of("/root/projects/converter");

    /**
     * <p>Using {@code createFileWithUniqueName} is necessary to create a path to a new file with unique name in multithreading environment.</p>
     *
     * <p>It is crucial because, in the case of several users send files with same names in the short period of time,
     * other options of creating a new file may produce {@link FileAlreadyExistsException}.
     * </p>
     *
     * @param fileName name of a file.
     *
     * @return found {@code Path} to a file.
     */
    private Path createFileWithUniqueName(String fileName) {
        Path destination = PROJECT_DIRECTORY_PATH.resolve(fileName);
        int count = 0;

        while (Files.exists(destination)) {
            count += 1;
            destination = PROJECT_DIRECTORY_PATH.resolve("%s (%s)".formatted(fileName, Integer.toString(count)));
        }

        return destination;
    }

    public Path convertJsonFileToCsv(MultipartFile jsonFile) throws IOException {
        String jsonFileName = jsonFile.getName();

        if (!jsonFileName.endsWith(".json")) {
            throw new UnsupportedExtensionException("Unsupported extension was provided");
        }

        String csvFileName = jsonFileName.substring(0, jsonFileName.length() - 5) + ".csv";
        Path uniquePath = createFileWithUniqueName(csvFileName);
        Path csvPath = Files.createTempFile(uniquePath.toString().
                substring(0, uniquePath.toString().length() - 4), ".csv");

        JsonNode root = new ObjectMapper().readTree(jsonFile.getInputStream());

        log.debug("Start converting json -> csv");
        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode headers = root.values().iterator().next();
        headers.propertyNames().forEach(csvSchemaBuilder::addColumn);
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();

        CsvMapper csvMapper = new CsvMapper();
        csvMapper.writerFor(JsonNode.class).with(csvSchema).writeValue(csvPath.toFile(), root);

        log.debug("Finished converting json -> csv");
        return csvPath;
    }
}
