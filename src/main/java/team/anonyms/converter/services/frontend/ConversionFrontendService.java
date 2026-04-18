package team.anonyms.converter.services.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvWriteException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.anonyms.converter.entities.Modification;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.utility.exceptions.IllegalPatternException;
import team.anonyms.converter.utility.exceptions.UnsupportedExtensionException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public final class ConversionFrontendService {
    private static final Logger log = LoggerFactory.getLogger(ConversionFrontendService.class);

    private final PatternService patternService;

    private final JsonMapper jsonMapper;
    private final XmlMapper xmlMapper;
    private final CsvMapper csvMapper;

    public ConversionFrontendService(
            PatternService patternService,
            JsonMapper jsonMapper,
            XmlMapper xmlMapper,
            CsvMapper csvMapper
    ) {
        this.patternService = patternService;

        this.jsonMapper = jsonMapper;
        this.xmlMapper = xmlMapper;
        this.csvMapper = csvMapper;
    }

    /**
     * <p>
     *     Counts number of occurrences for provided string and substring.
     * </p>
     *
     * @param string main string.
     * @param substring substring.
     *
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
     * {@code .json} or {@code .csv} are valid values for {@code currentExtension}, and {@code json}, {@code csv} are not valid.
     *
     * @return filename without provided {@code currentExtension}.
     *
     * @throws IllegalArgumentException if {@code currentExtension} doesn't contain the only "." at the beginning of itself.
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
     *     This method validates arguments for a conversion. It throws an exception if validation doesn't pass.
     * </p>
     *
     * @param file requested file to convert.
     * @param currentExtension current extension of {@code file}. {@code currentExtension} is supposed to start with dot.
     * For example, it is more preferably to send {@code .json} instead of {@code json} to this method despite the fact
     * that the validation doesn't fail in both cases.
     *
     * @throws NullPointerException if filename is null.
     * @throws UnsupportedExtensionException if {@code file} has extension, which doesn't correspond to {@code currentExtension}.
     */
    private void validateArgumentsForConversion(@NonNull MultipartFile file, @NonNull String currentExtension) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new NullPointerException("filename is null");
        }

        if (!filename.endsWith(currentExtension)) {
            throw new UnsupportedExtensionException("Provided file doesn't have '" + currentExtension + "' extension");
        }
    }

    /**
     * <p>
     *     Applies pattern to converted data.<br>
     *     <b>Assumption:</b> the number of additions of the same field is limited in quantity.
     *     All new fields are added only 1 time.
     * </p>
     *
     * @param rows converted data.
     * @param pattern any pattern to apply.
     *
     * @return {@code rows} after applying {@code pattern}.
     * If {@code pattern} is null provided {@code rows} will be returned.
     *
     * @throws IllegalPatternException if pattern contains modification with null or empty {@code oldName} and {@code newName} fields.
     */
    private @NonNull List<Map<String,Object>> applyPatterns(
            @NonNull List<Map<String,Object>> rows,
            @Nullable Pattern pattern
    ) {
        if (pattern == null) {
            return rows;
        }

        List<Modification> modifications = pattern.getModifications();
        List<Modification> addingModifications = new ArrayList<>();

        // Prepare to add new fields
        for (Modification modification : modifications) {
            if (modification.getOldName() == null) {
                if (modification.getNewName() == null) {
                    throw new IllegalPatternException(
                            "Modification with null or empty oldName and newName was provided; modification=" +
                                    modification
                    );
                }

                addingModifications.add(modification);
            }
        }

        for (Map<String, Object> row : rows) {
            int actualIndexOfModification = 0;

            for (int i = 0; i < modifications.size() - addingModifications.size(); i++) {
                actualIndexOfModification += i;
                Modification modification = modifications.get(actualIndexOfModification);

                // Deleting fields
                if (row.containsKey(modification.getOldName()) && (modification.getNewName() == null) &&
                        (modification.getNewType() == null) && (modification.getNewValue() == null)
                ) {
                    row.remove(modification.getOldName());
                    continue;
                }

                String fieldNameForTypeConversion = "";
                boolean isAddingIteration = false; // flag

                // Adding new fields
                if (modification.getOldName() == null) {
                    isAddingIteration = true;
                    fieldNameForTypeConversion = modification.getNewName();


                    row.put(fieldNameForTypeConversion, modification.getNewValue());
                    modifications.remove(modification);
                }

                // Altering existing fields
                if (row.containsKey(modification.getOldName())) {
                    // Changing values of fields
                    if (modification.getNewValue() != null) {
                        row.put(modification.getOldName(), modification.getNewValue());
                        fieldNameForTypeConversion = modification.getOldName();
                    }

                    // Changing names of fields
                    if (modification.getNewName() != null) {
                        Object value = row.get(modification.getOldName());
                        row.put(modification.getNewName(), value);
                        row.remove(modification.getOldName());

                        modification.setOldName(modification.getNewName());
                        fieldNameForTypeConversion = modification.getNewName();
                    }
                }

                // Type conversion
                if ((row.containsKey(modification.getOldName()) && (modification.getNewType() != null)) || isAddingIteration) {
                    Object value = row.get(fieldNameForTypeConversion);

                    if (value == null) {
                        continue;
                    }

                    // Find how to remove this hard code and add enum to db.
                    switch (modification.getNewType()) {
                        case "Integer":
                            row.put(fieldNameForTypeConversion, Integer.parseInt(value.toString()));
                            break;
                        case "Float":
                            row.put(fieldNameForTypeConversion, Float.parseFloat(value.toString()));
                            break;
                        case "Boolean":
                            row.put(fieldNameForTypeConversion, Boolean.parseBoolean(value.toString()));
                            break;
                        default:
                            row.put(fieldNameForTypeConversion, value.toString());
                            break;
                    }
                }
            }
        }

        return rows;
    }

    /**
     * <p>
     *     Converts JSON file to CSV file. Any other extensions are not supported.<br>
     *     <b>Assumption</b>: all nested objects in JSON file will be written to CSV file as strings.
     * </p>
     *
     * @param jsonFile JSON file written in {@link MultipartFile} instance.
     * @param patternId ID of a pattern, which already exists in database.
     *
     * @return path to converted CSV file.
     *
     * @throws IllegalArgumentException if {@code jsonFile} consists of
     * unsupported structure for conversion from JSON to CSV.
     * @throws NullPointerException if filename is null.
     * @throws UnsupportedExtensionException if {@code jsonFile} was provided without '.json' extension.
     * @throws EntityNotFoundException if pattern is not found in database by {@code patternId}.
     * @throws IllegalPatternException if pattern contains modification with null or empty {@code oldName} and {@code newName} fields.
     */
    @SuppressWarnings(value = {"unchecked"})
    public @NonNull Path convertJsonFileToCsv(
            @NonNull MultipartFile jsonFile,
            UUID patternId
    ) throws IOException {
        validateArgumentsForConversion(jsonFile, ".json");

        // Possible vulnerability here
        // Create temporarily CSV file for writing converted data
        String filenameWithoutExtension = getFilenameWithoutExtension(jsonFile, ".json");
        Path csvPath = Files.createTempFile(filenameWithoutExtension, ".csv");

        if (jsonFile.isEmpty()) {
            return csvPath;
        }

        // Start converting
        List<Map<String,Object>> rows = new ArrayList<>();

        JsonNode root;
        try {
            root = jsonMapper.readTree(jsonFile.getInputStream());
        } catch (JsonProcessingException e) {
            log.error("convertJsonFileToCsv: JsonProcessingException has been thrown");
            throw new IllegalArgumentException(e.getMessage());
        }

        if (root.isArray()) {
            for (JsonNode node : root) {
                rows.add(jsonMapper.convertValue(node, Map.class));
            }
        } else if (root.isObject()) {
            rows.add(jsonMapper.convertValue(root, Map.class));
        } else {
            throw new IllegalArgumentException("Unsupported JSON structure for CSV conversion");
        }

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("JSON file contains no rows to convert");
        }

        rows = applyPatterns(rows, patternService.findPatternById(patternId));

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
                    entry.setValue(jsonMapper.writeValueAsString(value));
                } else if (value == null) {
                    entry.setValue("");
                }
            }
        }

        // Writing converted data
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();

        try {
            csvMapper.writerFor(List.class).with(csvSchema).writeValue(csvPath.toFile(), rows);
        } catch (CsvWriteException e) {
            log.error("convertJsonFileToCsv: CsvWriteException has been thrown");
            throw new IllegalArgumentException(e.getMessage());
        }

        return csvPath;
    }

    /**
     * <p>
     *     Converts CSV file to JSON file. Any other extensions are not supported.
     * </p>
     *
     * @param csvFile CSV file written in {@link MultipartFile} instance.
     * @param patternId ID of a pattern, which already exists in database.
     *
     * @return path to converted JSON file.
     *
     * @throws IllegalArgumentException if {@code csvFile} consists of
     * unsupported structure for conversion from CSV to JSON.
     * @throws NullPointerException if filename is null.
     * @throws UnsupportedExtensionException if {@code csvFile} was provided without '.csv' extension.
     * @throws EntityNotFoundException if pattern is not found in database by {@code patternId}.
     * @throws IllegalPatternException if pattern contains modification with null or empty {@code oldName} and {@code newName} fields.
     */
    //fix separator problem
    public @NonNull Path convertCsvFileToJson(
            @NonNull MultipartFile csvFile,
            UUID patternId
    ) throws IOException {
        validateArgumentsForConversion(csvFile, ".csv");

        // Create temporarily JSON file for writing converted data
        String filenameWithoutExtension = getFilenameWithoutExtension(csvFile, ".csv");
        Path jsonPath = Files.createTempFile(filenameWithoutExtension, ".json");

        if (csvFile.isEmpty()) {
            return jsonPath;
        }

        // Start converting
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();

        List<Map<String, Object>> rows = new ArrayList<>();
        MappingIterator<Map<String, String>> iterator = csvMapper.readerFor(Map.class).
                with(csvSchema).readValues(csvFile.getInputStream());

        while (iterator.hasNext()) {
            Map<String, String> row = iterator.next();
            Map<String, Object> convertedRow = new LinkedHashMap<>();

            // Convert data types
            for (Map.Entry<String, String> entry : row.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value == null || value.isEmpty()) {
                    convertedRow.put(key, null);
                    continue;
                }

                try {
                    if (value.startsWith("{") || value.startsWith("[")) {
                        JsonNode node = csvMapper.readTree(value);
                        if (node.isObject()) {
                            convertedRow.put(key, csvMapper.convertValue(node, Map.class));
                        } else if (node.isArray()) {
                            convertedRow.put(key, csvMapper.convertValue(node, List.class));
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

        rows = applyPatterns(rows, patternService.findPatternById(patternId));

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("CSV file contains no rows to convert");
        }

        // Writing converted data
        if (rows.size() == 1) {
            jsonMapper.writeValue(jsonPath.toFile(), rows.getFirst());
        } else {
            jsonMapper.writeValue(jsonPath.toFile(), rows);
        }

        return jsonPath;
    }

    /**
     * <p>
     *     Converts JSON file to XML file. Any other extensions are not supported.
     * </p>
     *
     * @param jsonFile JSON file written in {@link MultipartFile} instance.
     * @param patternId ID of a pattern, which already exists in database.
     *
     * @return path to converted XML file.
     *
     * @throws IllegalArgumentException if {@code jsonFile} consists of
     * unsupported structure for conversion from JSON to XML.
     * @throws NullPointerException if filename is null.
     * @throws UnsupportedExtensionException if {@code jsonFile} was provided without '.json' extension.
     * @throws EntityNotFoundException if pattern is not found in database by {@code patternId}.
     * @throws IllegalPatternException if pattern contains modification with null or empty {@code oldName} and {@code newName} fields.
     */
    @SuppressWarnings(value = {"unchecked"})
    public @NonNull Path convertJsonFileToXml(
            @NonNull MultipartFile jsonFile,
            UUID patternId
    ) throws IOException {
        validateArgumentsForConversion(jsonFile, ".json");

        // Create temporarily XML file for writing converted data
        String filenameWithoutExtension = getFilenameWithoutExtension(jsonFile, ".json");
        Path xmlPath = Files.createTempFile(filenameWithoutExtension, ".xml");

        if (jsonFile.isEmpty()) {
            return xmlPath;
        }

        // Start converting
        List<Map<String,Object>> rows = new ArrayList<>();
        JsonNode root = jsonMapper.readTree(jsonFile.getInputStream());

        if (root.isArray()) {
            for (JsonNode node : root) {
                rows.add(jsonMapper.convertValue(node, Map.class));
            }
        } else if (root.isObject()) {
            rows.add(jsonMapper.convertValue(root, Map.class));
        } else {
            throw new IllegalArgumentException("Unsupported JSON structure for XML conversion");
        }

        rows = applyPatterns(rows, patternService.findPatternById(patternId));

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("JSON file contains no rows to convert");
        }

        // Writing converted data
        xmlMapper.writeValue(xmlPath.toFile(), rows);

        return xmlPath;
    }

    /**
     * <p>
     *     Converts XML file to JSON file. Any other extensions are not supported.
     * </p>
     *
     * @param xmlFile XML file written in {@link MultipartFile} instance.
     * @param patternId ID of a pattern, which already exists in database.
     *
     * @return path to converted JSON file.
     *
     * @throws IllegalArgumentException if {@code xmlFile} consists of
     * unsupported structure for conversion from XML to JSON.
     * @throws NullPointerException if filename is null.
     * @throws UnsupportedExtensionException if {@code xmlFile} was provided without '.xml' extension.
     * @throws EntityNotFoundException if pattern is not found in database by {@code patternId}.
     * @throws IllegalPatternException if pattern contains modification with null or empty {@code oldName} and {@code newName} fields.
     */
    @SuppressWarnings(value = {"unchecked"})
    public @NonNull Path convertXmlFileToJson(
            @NonNull MultipartFile xmlFile,
            UUID patternId
    ) throws IOException {
        validateArgumentsForConversion(xmlFile, ".xml");

        // Create temporarily JSON file for writing converted data
        String filenameWithoutExtension = getFilenameWithoutExtension(xmlFile, ".xml");
        Path jsonPath = Files.createTempFile(filenameWithoutExtension, ".json");

        if (xmlFile.isEmpty()) {
            return jsonPath;
        }

        // Start converting
        List<Map<String,Object>> rows = new ArrayList<>();

        JsonNode root;
        try {
            root = xmlMapper.readTree(xmlFile.getInputStream());
        } catch (JsonProcessingException e) {
            log.error("convertXmlFileToJson: JsonProcessingException has been thrown");
            throw new IllegalArgumentException(e.getMessage());
        }

        if (root.isObject() && root.size() == 1 && root.has("item")) {
            root = root.get("item");
        }

        if (root.isArray()) {
            for (JsonNode node : root) {
                rows.add(xmlMapper.convertValue(node, Map.class));
            }
        } else if (root.isObject()) {
            Map<String, Object> map = xmlMapper.convertValue(root, Map.class);
            if (!map.isEmpty()) {
                rows.add(map);
            }
        } else {
            throw new IllegalArgumentException("Unsupported XML structure for JSON conversion");
        }

        // Convert data types
        for (Map<String, Object> row : rows) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof String stringValue) {
                    if (stringValue.isEmpty()) {
                        entry.setValue(null);
                        continue;
                    }

                    try {
                        if (stringValue.matches("-?\\d+")) {
                            entry.setValue(Long.parseLong(stringValue));
                        } else if (stringValue.matches("-?\\d*\\.\\d+")) {
                            entry.setValue(Double.parseDouble(stringValue));
                        }
                    } catch (NumberFormatException _) {}
                }
            }
        }

        rows = applyPatterns(rows, patternService.findPatternById(patternId));

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("XML file contains no rows to convert");
        }

        // Writing converted data
        if (rows.size() == 1) {
            jsonMapper.writeValue(jsonPath.toFile(), rows.getFirst());
        } else {
            jsonMapper.writeValue(jsonPath.toFile(), rows);
        }

        return jsonPath;
    }

    /**
     * <p>
     *     Converts XML file to CSV file. Any other extensions are not supported.<br>
     *     <b>Assumption</b>: all nested objects in XML file will be written to CSV file as strings.
     * </p>
     *
     * @param xmlFile XML file written in {@link MultipartFile} instance.
     * @param patternId ID of a pattern, which already exists in database.
     *
     * @return path to converted CSV file.
     *
     * @throws IllegalArgumentException if {@code xmlFile} consists of
     * unsupported structure for conversion from XML to CSV.
     * @throws NullPointerException if filename is null.
     * @throws UnsupportedExtensionException if {@code xmlFile} was provided without '.xml' extension.
     * @throws EntityNotFoundException if pattern is not found in database by {@code patternId}.
     * @throws IllegalPatternException if pattern contains modification with null or empty {@code oldName} and {@code newName} fields.
     */
    @SuppressWarnings(value = {"unchecked"})
    public @NonNull Path convertXmlFileToCsv(
            @NonNull MultipartFile xmlFile,
            UUID patternId
    ) throws IOException {
        validateArgumentsForConversion(xmlFile, ".xml");

        // Possible vulnerability here
        // Create temporarily CSV file for writing converted data
        String filenameWithoutExtension = getFilenameWithoutExtension(xmlFile, ".xml");
        Path csvPath = Files.createTempFile(filenameWithoutExtension, ".csv");

        if (xmlFile.isEmpty()) {
            return csvPath;
        }

        // Start converting
        List<Map<String,Object>> rows = new ArrayList<>();

        JsonNode root;
        try {
            root = xmlMapper.readTree(xmlFile.getInputStream());
        } catch (JsonProcessingException e) {
            log.error("convertXmlFileToCsv: JsonProcessingException has been thrown");
            throw new IllegalArgumentException(e.getMessage());
        }

        if (root.isObject() && root.size() == 1 && root.has("item")) {
            root = root.get("item");
        }

        if (root.isArray()) {
            for (JsonNode node : root) {
                rows.add(xmlMapper.convertValue(node, Map.class));
            }
        } else if (root.isObject()) {
            rows.add(xmlMapper.convertValue(root, Map.class));
        } else {
            throw new IllegalArgumentException("Unsupported XML structure for CSV conversion");
        }

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("XML file contains no rows to convert");
        }

        // Converting nested structures to XML strings
        for (Map<String, Object> row : rows) {
            if (row == null) {
                continue;
            }

            for (Map.Entry<String, Object> entry : new ArrayList<>(row.entrySet())) {
                Object value = entry.getValue();

                if (value instanceof Map || value instanceof Collection) {
                    entry.setValue(xmlMapper.writeValueAsString(value));
                } else if (value == null) {
                    entry.setValue("");
                }
            }
        }

        rows = applyPatterns(rows, patternService.findPatternById(patternId));

        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        for (String column : rows.getFirst().keySet()) {
            csvSchemaBuilder.addColumn(column);
        }

        // Writing converted data
        try {
            CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
            csvMapper.writerFor(List.class).with(csvSchema).writeValue(csvPath.toFile(), rows);
        } catch (CsvWriteException e) {
            log.error("convertXmlFileToCsv: CsvWriteException has been thrown");
            throw new IllegalArgumentException(e.getMessage());
        }

        return csvPath;
    }

    /**
     * <p>
     *     Converts CSV file to XML file. Any other extensions are not supported.
     * </p>
     *
     * @param csvFile CSV file written in {@link MultipartFile} instance.
     * @param patternId ID of a pattern, which already exists in database.
     *
     * @return path to converted XML file.
     *
     * @throws IllegalArgumentException if {@code csvFile} consists of
     * unsupported structure for conversion from CSV to XML.
     * @throws NullPointerException if filename is null.
     * @throws UnsupportedExtensionException if {@code csvFile} was provided without '.csv' extension.
     * @throws EntityNotFoundException if pattern is not found in database by {@code patternId}.
     * @throws IllegalPatternException if pattern contains modification with null or empty {@code oldName} and {@code newName} fields.
     */
    //fix separator problem
    public @NonNull Path convertCsvFileToXml(
            @NonNull MultipartFile csvFile,
            UUID patternId
    ) throws IOException {
        validateArgumentsForConversion(csvFile, ".csv");

        // Create temporarily XML file for writing converted data
        String filenameWithoutExtension = getFilenameWithoutExtension(csvFile, ".csv");
        Path xmlPath = Files.createTempFile(filenameWithoutExtension, ".xml");

        if (csvFile.isEmpty()) {
            return xmlPath;
        }

        // Start converting
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
                        JsonNode node = csvMapper.readTree(value);
                        if (node.isObject()) {
                            convertedRow.put(key, csvMapper.convertValue(node, Map.class));
                        } else if (node.isArray()) {
                            convertedRow.put(key, csvMapper.convertValue(node, List.class));
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

        rows = applyPatterns(rows, patternService.findPatternById(patternId));

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("CSV file contains no rows to convert");
        }

        // Writing converted data
        if (rows.size() == 1) {
            xmlMapper.writeValue(xmlPath.toFile(), rows.getFirst());
        } else {
            xmlMapper.writeValue(xmlPath.toFile(), rows);
        }

        return xmlPath;
    }
}