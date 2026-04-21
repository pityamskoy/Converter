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

class ConversionJsonToXmlTest {

    private PatternService patternService;
    private ConversionFrontendService conversionFrontendService;

    private MockMultipartFile mockFile;
    private static final String inputJson = """
        [
          {
            "id": 1,
            "name": "Alice",
            "age": 30,
            "role": "admin",
            "value": 1
          },
          {
            "id": 2,
            "name": "Bob",
            "age": 25,
            "role": "user",
            "value": 0
          },
          {
            "id": 3,
            "name": "Charlie",
            "age": 35,
            "role": "moderator",
            "value": 1
          }
        ]
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
                "test.json",
                "application/json",
                inputJson.getBytes()
        );
    }

    @Test
    void testConvertJsonFileToXml_IllegalPatternException() {
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
            conversionFrontendService.convertJsonFileToXml(mockFile, patternId);
        });
        assertEquals("Modification with null or empty oldName and newName was provided; " +
                "modification=" + modification, exception.getMessage());
    }

    @Test
    void testConvertJsonFileToXml_NewField_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);

        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<score/>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertJsonFileToXml_NewField_WithValue_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);

        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<score>50</score>"));


        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertJsonFileToXml_NewField_WithType_WithValue_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);

        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<score>5267</score>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertJsonFileToXml_RemoveField_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);

        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<age>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertJsonFileToXml_ChangeValue_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);

        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<age>48</age>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertJsonFileToXml_ChangeType_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);

        String resultXml = Files.readString(resultXmlPath);
        System.out.println(resultXml);

        assertTrue(resultXml.contains("<value>false</value>"));
        assertTrue(resultXml.contains("<value>true</value>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertJsonFileToXml_ChangeType_ChangeValue_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);

        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<age>true</age>"));
        assertFalse(resultXml.contains("<age>false</age>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertJsonFileToXml_RenameField_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        System.out.println(resultXml);

        assertFalse(resultXml.contains("<age>"));
        assertTrue(resultXml.contains("<years_old>35</years_old>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertJsonFileToXml_RenameField_ChangeType_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);
        System.out.println(resultXml);

        assertFalse(resultXml.contains("<value>"));
        assertTrue(resultXml.contains("<is_passed>false</is_passed>"));
        assertTrue(resultXml.contains("<is_passed>true</is_passed>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertJsonFileToXml_RenameField_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "name",
                "nickname",
                null,
                "OlegMongol"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<name>Alice</name>"));
        assertFalse(resultXml.contains("<name>Bob</name>"));
        assertFalse(resultXml.contains("<name>Charlie</name>"));
        assertTrue(resultXml.contains("<nickname>OlegMongol</nickname>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertJsonFileToXml_RenameField_ChangeType_ChangeValue_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);

        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<age>"));
        assertTrue(resultXml.contains("<BOOL>50.5</BOOL>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertJsonFileToXml_MultipleModifications_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertJsonFileToXml(mockFile, patternId);

        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<age>true</age>"));
        assertTrue(resultXml.contains("<grade>5</grade>"));
        assertFalse(resultXml.contains("<age>false</age>"));

        Files.deleteIfExists(resultXmlPath);

    }
}