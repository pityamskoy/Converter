package team.anonyms.converter.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import team.anonyms.converter.entities.Modification;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;
import team.anonyms.converter.services.frontend.ConversionFrontendService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversionFrontendMassTest {
    private final PatternRepository patternRepository = Mockito.mock(PatternRepository.class);
    private final ModificationRepository modificationRepository = Mockito.mock(ModificationRepository.class);

    private final ConversionFrontendService service = new ConversionFrontendService(
            patternRepository, modificationRepository, new JsonMapper(), new XmlMapper(), new CsvMapper()
    );

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();
    private final CsvMapper csvMapper = new CsvMapper();

    private static final String BASIC_DIR = "src/test/resources/1. Basic Conversion/";
    private static final String PATTERNS_DIR = "src/test/resources/2. Conversion With Patterns/";

    static Stream<Arguments> testCases() {
        Stream.Builder<Arguments> builder = Stream.builder();
        for (int i = 1; i <= 40; i++) {
            builder.add(Arguments.of(i, false, BASIC_DIR));
        }
        for (int i = 1; i <= 40; i++) {
            builder.add(Arguments.of(i, true, PATTERNS_DIR));
        }
        return builder.build();
    }

    private Path getExpectedPath(String baseDir, int number, String extension) {
        String folderName = extension.substring(1);
        return Paths.get(baseDir + folderName + "/test_file_" + number + extension);
    }

    private MockMultipartFile getMockFile(String baseDir, int number, String extension, String contentType) throws IOException {
        Path path = getExpectedPath(baseDir, number, extension);
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile("file", path.getFileName().toString(), contentType, content);
    }

    private UUID setupPatternMock(int fileNumber, String baseDir) throws IOException {
        UUID patternId = UUID.randomUUID();
        Path patternPath = Paths.get(baseDir + "patterns/pattern_" + fileNumber + ".json");

        Assumptions.assumeTrue(Files.exists(patternPath), "Pattern file not found: " + patternPath);

        List<Map<String, String>> modsData = jsonMapper.readValue(patternPath.toFile(), new TypeReference<>() {});
        List<Modification> mockedMods = new ArrayList<>();

        for (Map<String, String> data : modsData) {
            Modification mod = Mockito.mock(Modification.class);
            if (data.containsKey("oldName")) Mockito.when(mod.getOldName()).thenReturn(data.get("oldName"));
            if (data.containsKey("newName")) Mockito.when(mod.getNewName()).thenReturn(data.get("newName"));
            if (data.containsKey("newType")) Mockito.when(mod.getNewType()).thenReturn(data.get("newType"));
            if (data.containsKey("newValue")) Mockito.when(mod.getNewValue()).thenReturn(data.get("newValue"));
            mockedMods.add(mod);
        }

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);
        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(mockedMods);

        return patternId;
    }

    private void assertJsonEquals(Path expectedFile, Path actualFile) throws IOException {
        JsonNode expected = jsonMapper.readTree(expectedFile.toFile());
        JsonNode actual = jsonMapper.readTree(actualFile.toFile());
        assertEquals(expected, actual, "JSON content isn't the same as expected");
    }

    private void assertXmlEquals(Path expectedFile, Path actualFile) throws IOException {
        JsonNode expected = xmlMapper.readTree(expectedFile.toFile());
        JsonNode actual = xmlMapper.readTree(actualFile.toFile());
        assertEquals(expected, actual, "XML content isn't the same as expected");
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
        assertEquals(expected, actual, "CSV content isn't the same as expected");
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testConvertJsonToCsv(int fileNumber, boolean usePattern, String baseDir) throws Exception {
        Assumptions.assumeFalse(usePattern, "Reverse conversion can't be applied");
        Path inputPath = getExpectedPath(baseDir, fileNumber, ".json");
        Assumptions.assumeTrue(Files.exists(inputPath), "Input file missing, skipping: " + inputPath);

        MockMultipartFile input = getMockFile(baseDir, fileNumber, ".json", "application/json");
        Path expected = getExpectedPath(baseDir, fileNumber, ".csv");
        Path actual = service.convertJsonFileToCsv(input, null);
        assertCsvEquals(expected, actual);
        Files.deleteIfExists(actual);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testConvertCsvToJson(int fileNumber, boolean usePattern, String baseDir) throws Exception {
        Path inputPath = getExpectedPath(baseDir, fileNumber, ".csv");
        Assumptions.assumeTrue(Files.exists(inputPath), "Input file missing, skipping: " + inputPath);

        MockMultipartFile input = getMockFile(baseDir, fileNumber, ".csv", "text/csv");
        Path expected = getExpectedPath(baseDir, fileNumber, ".json");
        UUID patternId = usePattern ? setupPatternMock(fileNumber, baseDir) : null;

        Path actual = service.convertCsvFileToJson(input, patternId);
        assertJsonEquals(expected, actual);
        Files.deleteIfExists(actual);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testConvertJsonToXml(int fileNumber, boolean usePattern, String baseDir) throws Exception {
        Assumptions.assumeFalse(usePattern, "Double pattern can't be applied");
        Path inputPath = getExpectedPath(baseDir, fileNumber, ".json");
        Assumptions.assumeTrue(Files.exists(inputPath), "Input file missing, skipping: " + inputPath);

        MockMultipartFile input = getMockFile(baseDir, fileNumber, ".json", "application/json");
        Path expected = getExpectedPath(baseDir, fileNumber, ".xml");
        Path actual = service.convertJsonFileToXml(input, null);
        assertXmlEquals(expected, actual);
        Files.deleteIfExists(actual);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testConvertXmlToJson(int fileNumber, boolean usePattern, String baseDir) throws Exception {
        Assumptions.assumeFalse(usePattern, "Double pattern can't be applied");
        Path inputPath = getExpectedPath(baseDir, fileNumber, ".xml");
        Assumptions.assumeTrue(Files.exists(inputPath), "Input file missing, skipping: " + inputPath);

        MockMultipartFile input = getMockFile(baseDir, fileNumber, ".xml", "application/xml");
        Path expected = getExpectedPath(baseDir, fileNumber, ".json");
        Path actual = service.convertXmlFileToJson(input, null);
        assertJsonEquals(expected, actual);
        Files.deleteIfExists(actual);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testConvertCsvToXml(int fileNumber, boolean usePattern, String baseDir) throws Exception {
        Path inputPath = getExpectedPath(baseDir, fileNumber, ".csv");
        Assumptions.assumeTrue(Files.exists(inputPath), "Input file missing, skipping: " + inputPath);

        MockMultipartFile input = getMockFile(baseDir, fileNumber, ".csv", "text/csv");
        Path expected = getExpectedPath(baseDir, fileNumber, ".xml");
        UUID patternId = usePattern ? setupPatternMock(fileNumber, baseDir) : null;

        Path actual = service.convertCsvFileToXml(input, patternId);
        assertXmlEquals(expected, actual);
        Files.deleteIfExists(actual);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testConvertXmlToCsv(int fileNumber, boolean usePattern, String baseDir) throws Exception {
        Assumptions.assumeFalse(usePattern, "Reverse conversion can't be applied");
        Path inputPath = getExpectedPath(baseDir, fileNumber, ".xml");
        Assumptions.assumeTrue(Files.exists(inputPath), "Input file missing, skipping: " + inputPath);

        MockMultipartFile input = getMockFile(baseDir, fileNumber, ".xml", "application/xml");
        Path expected = getExpectedPath(baseDir, fileNumber, ".csv");
        Path actual = service.convertXmlFileToCsv(input, null);
        assertCsvEquals(expected, actual);
        Files.deleteIfExists(actual);
    }
}