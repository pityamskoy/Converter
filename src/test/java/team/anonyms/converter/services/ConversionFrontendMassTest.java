package team.anonyms.converter.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import team.anonyms.converter.services.frontend.ConversionFrontendService;
import team.anonyms.converter.services.frontend.PatternService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversionFrontendMassTest {
    private final PatternService patternService = Mockito.mock(PatternService.class);

    private final ConversionFrontendService service = new ConversionFrontendService(
            patternService, new JsonMapper(), new XmlMapper(), new CsvMapper()
    );

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();
    private final CsvMapper csvMapper = new CsvMapper();

    private static final String BASE_DIR = "src/test/resources/test_files/";

    static IntStream fileNumbers() {
        return IntStream.rangeClosed(1, 2);
    }

    private Path getExpectedPath(int number, String extension) {
        String folderName = extension.substring(1);

        return Paths.get(BASE_DIR + folderName + "/test_file_" + number + extension);
    }

    private MockMultipartFile getMockFile(int number, String extension, String contentType) throws IOException {
        Path path = getExpectedPath(number, extension);
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile("file", path.getFileName().toString(), contentType, content);
    }

    private void assertJsonEquals(Path expectedFile, Path actualFile) throws IOException {
        JsonNode expected = jsonMapper.readTree(expectedFile.toFile());
        JsonNode actual = jsonMapper.readTree(actualFile.toFile());
        assertEquals(expected, actual, "Содержимое JSON не совпадает");
    }

    private void assertXmlEquals(Path expectedFile, Path actualFile) throws IOException {
        JsonNode expected = xmlMapper.readTree(expectedFile.toFile());
        JsonNode actual = xmlMapper.readTree(actualFile.toFile());
        assertEquals(expected, actual, "Содержимое XML не совпадает");
    }

    private void assertCsvEquals(Path expectedFile, Path actualFile) throws IOException {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        List<?> expected;
        try (com.fasterxml.jackson.databind.MappingIterator<?> it =
                     csvMapper.readerFor(Map.class).with(schema).readValues(expectedFile.toFile())) {
            expected = it.readAll();
        }

        List<?> actual;
        try (com.fasterxml.jackson.databind.MappingIterator<?> it =
                     csvMapper.readerFor(Map.class).with(schema).readValues(actualFile.toFile())) {
            actual = it.readAll();
        }

        assertEquals(expected, actual, "Содержимое CSV не совпадает");
    }

    @ParameterizedTest
    @MethodSource("fileNumbers")
    void testConvertJsonToCsv(int fileNumber) throws Exception {
        MockMultipartFile input = getMockFile(fileNumber, ".json", "application/json");
        Path expected = getExpectedPath(fileNumber, ".csv");

        Path actual = service.convertJsonFileToCsv(input, null);
        assertCsvEquals(expected, actual);
        Files.deleteIfExists(actual);
    }

    @ParameterizedTest
    @MethodSource("fileNumbers")
    void testConvertCsvToJson(int fileNumber) throws Exception {
        MockMultipartFile input = getMockFile(fileNumber, ".csv", "text/csv");
        Path expected = getExpectedPath(fileNumber, ".json");

        Path actual = service.convertCsvFileToJson(input, null);
        assertJsonEquals(expected, actual);
        Files.deleteIfExists(actual);
    }

    @ParameterizedTest
    @MethodSource("fileNumbers")
    void testConvertJsonToXml(int fileNumber) throws Exception {
        MockMultipartFile input = getMockFile(fileNumber, ".json", "application/json");
        Path expected = getExpectedPath(fileNumber, ".xml");

        Path actual = service.convertJsonFileToXml(input, null);
        assertXmlEquals(expected, actual);
        Files.deleteIfExists(actual);
    }

    @ParameterizedTest
    @MethodSource("fileNumbers")
    void testConvertXmlToJson(int fileNumber) throws Exception {
        MockMultipartFile input = getMockFile(fileNumber, ".xml", "application/xml");
        Path expected = getExpectedPath(fileNumber, ".json");

        Path actual = service.convertXmlFileToJson(input, null);
        assertJsonEquals(expected, actual);
        Files.deleteIfExists(actual);
    }

    @ParameterizedTest
    @MethodSource("fileNumbers")
    void testConvertCsvToXml(int fileNumber) throws Exception {
        MockMultipartFile input = getMockFile(fileNumber, ".csv", "text/csv");
        Path expected = getExpectedPath(fileNumber, ".xml");

        Path actual = service.convertCsvFileToXml(input, null);
        assertXmlEquals(expected, actual);
        Files.deleteIfExists(actual);
    }

    @ParameterizedTest
    @MethodSource("fileNumbers")
    void testConvertXmlToCsv(int fileNumber) throws Exception {
        MockMultipartFile input = getMockFile(fileNumber, ".xml", "application/xml");
        Path expected = getExpectedPath(fileNumber, ".csv");

        Path actual = service.convertXmlFileToCsv(input, null);
        assertCsvEquals(expected, actual);
        Files.deleteIfExists(actual);
    }
}