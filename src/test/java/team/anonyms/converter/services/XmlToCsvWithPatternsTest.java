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

class ConversionXmlToCsvTest {

    private PatternRepository patternRepository;
    private ModificationRepository modificationRepository;
    private ConversionFrontendService conversionFrontendService;

    private MockMultipartFile mockFile;

    private static final String inputXml = """
        <ArrayList>
          <item>
            <id>1</id>
            <name>Alice</name>
            <age>30</age>
            <role>admin</role>
            <value>1</value>
          </item>
          <item>
            <id>2</id>
            <name>Bob</name>
            <age>25</age>
            <role>user</role>
            <value>0</value>
          </item>
          <item>
            <id>3</id>
            <name>Charlie</name>
            <age>35</age>
            <role>moderator</role>
            <value>1</value>
          </item>
        </ArrayList>
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
                "test.xml",
                "application/xml",
                inputXml.getBytes()
        );
    }

    @Test
    void testConvertXmlFileToCsv_IllegalPatternException() {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        IllegalPatternException exception = assertThrows(IllegalPatternException.class, () -> {
            conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        });

        assertTrue(exception.getMessage().contains("Modification with null or empty oldName and newName"));
    }

    @Test
    void testConvertXmlFileToCsv_NewField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getNewName()).thenReturn("score");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("score"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_NewField_WithValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getNewName()).thenReturn("score");
        Mockito.when(mockMod.getNewValue()).thenReturn("50");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("score"));
        assertTrue(resultCsv.contains("50"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_NewField_WithType_WithValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getNewName()).thenReturn("score");
        Mockito.when(mockMod.getNewType()).thenReturn("Integer");
        Mockito.when(mockMod.getNewValue()).thenReturn("5267");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("score"));
        assertTrue(resultCsv.contains("5267"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_RemoveField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertFalse(resultCsv.contains("age"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");
        Mockito.when(mockMod.getNewValue()).thenReturn("48");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("48"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_ChangeType_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("value");
        Mockito.when(mockMod.getNewType()).thenReturn("Boolean");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("false"));
        assertTrue(resultCsv.contains("true"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_ChangeType_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");
        Mockito.when(mockMod.getNewType()).thenReturn("Boolean");
        Mockito.when(mockMod.getNewValue()).thenReturn("true");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("true"));
        assertFalse(resultCsv.contains("false"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_RenameField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");
        Mockito.when(mockMod.getNewName()).thenReturn("years_old");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertFalse(resultCsv.contains("age"));
        assertTrue(resultCsv.contains("years_old"));
        assertTrue(resultCsv.contains("35"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_RenameField_ChangeType_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("value");
        Mockito.when(mockMod.getNewName()).thenReturn("is_passed");
        Mockito.when(mockMod.getNewType()).thenReturn("Boolean");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertFalse(resultCsv.contains("value"));
        assertTrue(resultCsv.contains("is_passed"));
        assertTrue(resultCsv.contains("false"));
        assertTrue(resultCsv.contains("true"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_RenameField_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("name");
        Mockito.when(mockMod.getNewName()).thenReturn("nick");
        Mockito.when(mockMod.getNewValue()).thenReturn("OlegMongol");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertFalse(resultCsv.contains("Alice"));
        assertFalse(resultCsv.contains("Bob"));
        assertFalse(resultCsv.contains("Charlie"));
        assertFalse(resultCsv.contains("name"));
        assertTrue(resultCsv.contains("nick"));
        assertTrue(resultCsv.contains("OlegMongol"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_RenameField_ChangeType_ChangeValue_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertFalse(resultCsv.contains("age"));
        assertTrue(resultCsv.contains("BOOL"));
        assertTrue(resultCsv.contains("50.5"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_MultipleModifications_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("true"));
        assertTrue(resultCsv.contains("grade"));
        assertTrue(resultCsv.contains("5"));
        assertFalse(resultCsv.contains("false"));

        Files.deleteIfExists(resultCsvPath);
    }
}