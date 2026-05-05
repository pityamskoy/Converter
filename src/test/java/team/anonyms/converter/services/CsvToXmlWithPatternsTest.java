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

class ConversionCsvToXmlTest {

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
    void testConvertCsvFileToXml_IllegalPatternException() {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        IllegalPatternException exception = assertThrows(
                IllegalPatternException.class,
                () -> conversionFrontendService.convertCsvFileToXml(mockFile, patternId)
        );

        assertTrue(exception.getMessage().contains("Modification with null or empty oldName and newName"));
    }

    @Test
    void testConvertCsvFileToXml_NewField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getNewName()).thenReturn("score");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<score/>") || resultXml.contains("<score></score>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_NewField_WithValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getNewName()).thenReturn("score");
        Mockito.when(mockMod.getNewValue()).thenReturn("50");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<score>50</score>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_NewField_WithType_WithValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getNewName()).thenReturn("score");
        Mockito.when(mockMod.getNewType()).thenReturn("Integer");
        Mockito.when(mockMod.getNewValue()).thenReturn("5267");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<score>5267</score>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_RemoveField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<age>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");
        Mockito.when(mockMod.getNewValue()).thenReturn("48");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<age>48</age>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_ChangeType_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("value");
        Mockito.when(mockMod.getNewType()).thenReturn("Boolean");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<value>false</value>"));
        assertTrue(resultXml.contains("<value>true</value>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_ChangeType_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");
        Mockito.when(mockMod.getNewType()).thenReturn("Boolean");
        Mockito.when(mockMod.getNewValue()).thenReturn("true");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<age>true</age>"));
        assertFalse(resultXml.contains("<age>false</age>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_RenameField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("age");
        Mockito.when(mockMod.getNewName()).thenReturn("years_old");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<age>"));
        assertTrue(resultXml.contains("<years_old>35</years_old>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_RenameField_ChangeType_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("value");
        Mockito.when(mockMod.getNewName()).thenReturn("is_passed");
        Mockito.when(mockMod.getNewType()).thenReturn("Boolean");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<value>"));
        assertTrue(resultXml.contains("<is_passed>false</is_passed>"));
        assertTrue(resultXml.contains("<is_passed>true</is_passed>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_RenameField_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification mockMod = Mockito.mock(Modification.class);
        Mockito.when(mockMod.getOldName()).thenReturn("name");
        Mockito.when(mockMod.getNewName()).thenReturn("nickname");
        Mockito.when(mockMod.getNewValue()).thenReturn("OlegMongol");

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findPatternById(patternId)).thenReturn(mockPattern);
        Mockito.when(modificationRepository.findAllByPatternId(patternId)).thenReturn(List.of(mockMod));

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<name>Alice</name>"));
        assertFalse(resultXml.contains("<name>Bob</name>"));
        assertFalse(resultXml.contains("<name>Charlie</name>"));
        assertTrue(resultXml.contains("<nickname>OlegMongol</nickname>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_RenameField_ChangeType_ChangeValue_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<age>"));
        assertTrue(resultXml.contains("<BOOL>50.5</BOOL>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_MultipleModifications_Success() throws Exception {
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

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<age>true</age>"));
        assertTrue(resultXml.contains("<grade>5</grade>"));
        assertFalse(resultXml.contains("<age>false</age>"));

        Files.deleteIfExists(resultXmlPath);
    }
}