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
import team.anonyms.converter.services.frontend.ConversionFrontendService;
import team.anonyms.converter.services.frontend.PatternService;
import team.anonyms.converter.utility.exceptions.IllegalPatternException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
/*
class ConversionXmlToCsvTest {

    private PatternService patternService;
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
        patternService = Mockito.mock(PatternService.class);

        JsonMapper jsonMapper = new JsonMapper();
        XmlMapper xmlMapper = new XmlMapper();
        CsvMapper csvMapper = new CsvMapper();

        conversionFrontendService = new ConversionFrontendService(
                patternService, jsonMapper, xmlMapper, csvMapper
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

        Modification modification = new Modification(
                UUID.randomUUID(),
                null,
                null,
                null,
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                List.of(modification)
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        IllegalPatternException exception = assertThrows(IllegalPatternException.class, () -> {
            conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        });
        assertEquals("Modification with null or empty oldName and newName was provided; " +
                "modification=" + modification, exception.getMessage());
    }

    @Test
    void testConvertXmlFileToCsv_NewField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                null,
                "score",
                null,
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("score"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_NewField_WithValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                null,
                "score",
                null,
                "50"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("score"));
        assertTrue(resultCsv.contains("50"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_NewField_WithType_WithValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                null,
                "score",
                "Integer",
                "5267"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("score"));
        assertTrue(resultCsv.contains("5267"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_RemoveField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "age",
                null,
                null,
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertFalse(resultCsv.contains("age"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "age",
                null,
                null,
                "48"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("48"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_ChangeType_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "value",
                null,
                "Boolean",
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("false"));
        assertTrue(resultCsv.contains("true"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_ChangeType_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "age",
                null,
                "Boolean",
                "true"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("true"));
        assertFalse(resultCsv.contains("false"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertXmlFileToCsv_RenameField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "age",
                "years_old",
                null,
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

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

        Modification modification = new Modification(
                UUID.randomUUID(),
                "value",
                "is_passed",
                "Boolean",
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

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

        Modification modification = new Modification(
                UUID.randomUUID(),
                "name",
                "nick",
                null,
                "OlegMongol"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

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

        Modification modification = new Modification(
                UUID.randomUUID(),
                "age",
                "BOOL",
                "Float",
                "50.5"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                List.of(modification)
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

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

        Modification modification_first = new Modification(
                UUID.randomUUID(),
                "age",
                null,
                "Boolean",
                "true"
        );

        Modification modification_second = new Modification(
                UUID.randomUUID(),
                null,
                "grade",
                "Integer",
                "5"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification_first, modification_second))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultCsvPath = conversionFrontendService.convertXmlFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("true"));
        assertTrue(resultCsv.contains("grade"));
        assertTrue(resultCsv.contains("5"));
        assertFalse(resultCsv.contains("false"));

        Files.deleteIfExists(resultCsvPath);
    }
}*/