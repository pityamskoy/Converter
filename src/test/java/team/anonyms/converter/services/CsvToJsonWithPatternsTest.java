package team.anonyms.converter.services;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import team.anonyms.converter.entities.Modification;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.exceptions.IllegalPatternException;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;
import team.anonyms.converter.services.frontend.ConversionFrontendService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConversionCsvToJsonTest {

    private PatternRepository patternRepository;
    private ModificationRepository modificationRepository;
    private ConversionFrontendService conversionFrontendService;

    private MockMultipartFile mockFile;

    private static final String inputCsv = """
        id,name,age,role,value
        1,Alice,30,admin,1
        2,Bob,25,user,0
        3,Charlie,35,moderator,1
        """;

    @BeforeEach
    void setUp() {
        patternRepository = Mockito.mock(PatternRepository.class);
        modificationRepository = Mockito.mock(ModificationRepository.class);

        JsonMapper jsonMapper = new JsonMapper();
        XmlMapper xmlMapper = new XmlMapper();
        CsvMapper csvMapper = new CsvMapper();

        conversionFrontendService = new ConversionFrontendService(
                patternRepository, modificationRepository, jsonMapper, xmlMapper, csvMapper
        );

        mockFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                inputCsv.getBytes()
        );
    }

    @Test
    void testConvertCsvFileToJson_IllegalPatternException() {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        IllegalPatternException exception = assertThrows(IllegalPatternException.class, () -> {
            conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        });

        assertTrue(exception.getMessage().contains("Modification with null or empty oldName and newName"));
    }

    @Test
    void testConvertCsvFileToJson_NewField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getNewName()).thenReturn("score");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"score\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_NewField_WithValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getNewName()).thenReturn("score");
        Mockito.when(mockMod.getNewValue()).thenReturn("50");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"score\""));
        assertTrue(resultJson.contains("\"50\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_NewField_WithType_WithValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getNewName()).thenReturn("score");
        Mockito.when(mockMod.getNewType()).thenReturn("Integer");
        Mockito.when(mockMod.getNewValue()).thenReturn("5267");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"score\""));
        assertTrue(resultJson.contains("5267"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_RemoveField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"age\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");
        Mockito.when(mockMod.getNewValue()).thenReturn("48");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"48\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_ChangeType_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("value");
        Mockito.when(mockMod.getNewType()).thenReturn("Boolean");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("false"));
        assertTrue(resultJson.contains("true"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_ChangeType_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");
        Mockito.when(mockMod.getNewType()).thenReturn("Boolean");
        Mockito.when(mockMod.getNewValue()).thenReturn("true");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("true"));
        assertFalse(resultJson.contains("false"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_RenameField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");
        Mockito.when(mockMod.getNewName()).thenReturn("years_old");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"age\""));
        assertTrue(resultJson.contains("\"years_old\""));
        assertTrue(resultJson.contains("35"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_RenameField_ChangeType_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("value");
        Mockito.when(mockMod.getNewName()).thenReturn("is_passed");
        Mockito.when(mockMod.getNewType()).thenReturn("Boolean");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"value\""));
        assertTrue(resultJson.contains("\"is_passed\""));
        assertTrue(resultJson.contains("false"));
        assertTrue(resultJson.contains("true"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_RenameField_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("name");
        Mockito.when(mockMod.getNewName()).thenReturn("nickname");
        Mockito.when(mockMod.getNewValue()).thenReturn("OlegMongol");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"Alice\""));
        assertFalse(resultJson.contains("\"Bob\""));
        assertFalse(resultJson.contains("\"Charlie\""));
        assertFalse(resultJson.contains("\"name\""));

        assertTrue(resultJson.contains("\"nickname\""));
        assertTrue(resultJson.contains("\"OlegMongol\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_RenameField_ChangeType_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");
        Mockito.when(mockMod.getNewName()).thenReturn("BOOL");
        Mockito.when(mockMod.getNewType()).thenReturn("Float");
        Mockito.when(mockMod.getNewValue()).thenReturn("50.5");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"age\""));
        assertTrue(resultJson.contains("\"BOOL\""));
        assertTrue(resultJson.contains("50.5"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_MultipleModifications_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockModFirst = Mockito.mock(Modification.class);
        Mockito.when(mockModFirst.getOldName()).thenReturn("age");
        Mockito.when(mockModFirst.getNewType()).thenReturn("Boolean");
        Mockito.when(mockModFirst.getNewValue()).thenReturn("true");

        Modification mockModSecond = Mockito.mock(Modification.class);
        Mockito.when(mockModSecond.getNewName()).thenReturn("grade");
        Mockito.when(mockModSecond.getNewType()).thenReturn("Integer");
        Mockito.when(mockModSecond.getNewValue()).thenReturn("5");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId))
                .thenReturn(List.of(mockModFirst, mockModSecond));

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("true"));
        assertTrue(resultJson.contains("\"grade\""));
        assertTrue(resultJson.contains("5"));
        assertFalse(resultJson.contains("false"));

        Files.deleteIfExists(resultJsonPath);
    }
}