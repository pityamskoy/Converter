package team.anonyms.converter.services;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import team.anonyms.converter.exceptions.UnsupportedExtensionException;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;
import team.anonyms.converter.services.frontend.ConversionFrontendService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConversionFrontendServiceTest {

    private final PatternRepository patternRepository = Mockito.mock(PatternRepository.class);
    private final ModificationRepository modificationRepository = Mockito.mock(ModificationRepository.class);

    private final ConversionFrontendService conversionFrontendService = new ConversionFrontendService(
            patternRepository,
            modificationRepository,
            new JsonMapper(),
            new XmlMapper(),
            new CsvMapper()
    );

    @Test
    void testConvertJsonFileToCsv_Success() throws IOException {
        String jsonContent = "[{" +
                "\"name\":\"Ivan\",\"age\":30" +
                "}," +
                "{" +
                "\"name\":\"Anna\",\"age\":25" +
                "}]";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                jsonContent.getBytes()
        );

        Path resultPath = conversionFrontendService.convertJsonFileToCsv(mockFile, null);

        assertNotNull(resultPath);
        assertTrue(Files.exists(resultPath));

        String csvContent = Files.readString(resultPath);

        assertTrue(csvContent.contains("name"));
        assertTrue(csvContent.contains("age"));
        assertTrue(csvContent.contains("Ivan"));
        assertTrue(csvContent.contains("30"));
        assertTrue(csvContent.contains("Anna"));

        Files.deleteIfExists(resultPath);
    }

    @Test
    void testConvertJsonFileToCsv_UnsupportedJSONStructure_ThrowsException() {
        String jsonContent = "[{" +
                "\"name\":\"Муся\"\"age\":30000" +
                "}," +
                "{" +
                "\"name\":\"Маруся\",\"age\":30000" +
                "}]";

        MockMultipartFile brokenFile = new MockMultipartFile(
                "file",
                "test_file.json",
                "application/json",
                jsonContent.getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            conversionFrontendService.convertJsonFileToCsv(brokenFile, null);
        });
    }

    @Test
    void testConvertJsonFileToCsv_NoRows_ThrowsException() {
        String jsonContent = "[{}]";

        MockMultipartFile brokenFile = new MockMultipartFile(
                "file",
                "test_file.json",
                "application/json",
                jsonContent.getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            conversionFrontendService.convertJsonFileToCsv(brokenFile, null);
        });
    }

    @Test
    void testConvertCsvFileToJson_Success() throws IOException {
        String csvContent = "name,age\nIvan,30\nAnna,25";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        Path resultPath = conversionFrontendService.convertCsvFileToJson(mockFile, null);

        assertNotNull(resultPath);
        assertTrue(Files.exists(resultPath));

        String jsonContent = Files.readString(resultPath);

        assertTrue(jsonContent.contains("\"name\":\"Ivan\""));
        assertTrue(jsonContent.contains("\"age\":30"));
        assertTrue(jsonContent.contains("\"name\":\"Anna\""));

        Files.deleteIfExists(resultPath);
    }

    @Test
    void testConvertCsvFileToJson_NoRows_ThrowsException() {
        String csvContent = "Олег,Монгол";
        MockMultipartFile brokenFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionFrontendService.convertCsvFileToJson(brokenFile, null);
        });
        assertEquals("CSV file contains no rows to convert", exception.getMessage());
    }

    @Test
    void testValidateArgumentsForConversion_AndReturnPattern_NullFilename_ThrowsException() {
        MultipartFile brokenFile = Mockito.mock(MultipartFile.class);

        Mockito.when(brokenFile.isEmpty()).thenReturn(false);
        Mockito.when(brokenFile.getOriginalFilename()).thenReturn(null);

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            conversionFrontendService.convertJsonFileToCsv(brokenFile, null);
        });
        assertEquals("filename is null", exception.getMessage());
    }

    @Test
    void testValidateArgumentsForConversion_AndReturnPattern_WrongExtension_ThrowsException() {
        MockMultipartFile txtFile = new MockMultipartFile(
                "file",
                "wrong.txt",
                "text/plain",
                "data".getBytes()
        );

        UnsupportedExtensionException exception = assertThrows(UnsupportedExtensionException.class, () -> {
            conversionFrontendService.convertJsonFileToCsv(txtFile, null);
        });

        assertEquals("Provided file doesn't have '.json' extension", exception.getMessage());
    }

    @Test
    void testConvertJsonFileToXml_Success() throws IOException {
        String jsonContent = "[{" +
                "\"name\":\"Belkin Sergey\",\"age\":18" +
                "}," +
                "{" +
                "\"name\":\"Krylov Daniil\",\"age\":19" +
                "}]";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                jsonContent.getBytes()
        );

        Path resultPath = conversionFrontendService.convertJsonFileToXml(mockFile, null);

        assertNotNull(resultPath);
        assertTrue(Files.exists(resultPath));

        String xmlContent = Files.readString(resultPath);

        assertTrue(xmlContent.contains("<name>Belkin Sergey</name>"));
        assertTrue(xmlContent.contains("<age>18</age>"));
        assertTrue(xmlContent.contains("<name>Krylov Daniil</name>"));
        assertTrue(xmlContent.contains("<age>19</age>"));

        Files.deleteIfExists(resultPath);
    }

    @Test
    void testConvertJsonFileToXml_UnsupportedJSONStructure_ThrowsException() {
        String jsonContent = "[{" +
                "\"name\":\"Муся\"\"age\":30000" +
                "}]";

        MockMultipartFile brokenFile = new MockMultipartFile(
                "file",
                "test_file.json",
                "application/json",
                jsonContent.getBytes()
        );

        assertThrows(IOException.class, () -> {
            conversionFrontendService.convertJsonFileToXml(brokenFile, null);
        });
    }

    @Test
    void testConvertJsonFileToXml_NoRows_ThrowsException() {
        String jsonContent = "[]";

        MockMultipartFile brokenFile = new MockMultipartFile(
                "file",
                "test_file.json",
                "application/json",
                jsonContent.getBytes()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionFrontendService.convertJsonFileToXml(brokenFile, null);
        });

        assertEquals("JSON file contains no rows to convert", exception.getMessage());
    }

    @Test
    void testConvertXmlFileToJson_Success() throws IOException {
        String xmlContent = "<root>" +
                "<name>Kekishev Andrey</name>" +
                "<age>18</age>" +
                "</root>";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.xml",
                "application/xml",
                xmlContent.getBytes()
        );

        Path resultPath = conversionFrontendService.convertXmlFileToJson(mockFile, null);

        assertNotNull(resultPath);
        assertTrue(Files.exists(resultPath));

        String jsonContent = Files.readString(resultPath);

        assertTrue(jsonContent.contains("\"name\":\"Kekishev Andrey\""));
        assertTrue(jsonContent.contains("\"age\":\"18\"") || jsonContent.contains("\"age\":18"));

        Files.deleteIfExists(resultPath);
    }

    @Test
    void testConvertXmlFileToJson_UnsupportedXMLStructure_ThrowsException() {
        String xmlContent = "<root>" +
                "<name.Сломанная структура.name>" +
                "<age.Тут должны быть проблемы.age>" +
                "</root>";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.xml",
                "application/xml",
                xmlContent.getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            conversionFrontendService.convertXmlFileToJson(mockFile, null);
        });
    }

    @Test
    void testConvertXmlFileToJson_NoRows_ThrowsException() {
        String xmlContent = "<root></root>";

        MockMultipartFile noRowsFile = new MockMultipartFile(
                "file",
                "test.xml",
                "application/xml",
                xmlContent.getBytes()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionFrontendService.convertXmlFileToJson(noRowsFile, null);
        });

        assertEquals("XML file contains no rows to convert", exception.getMessage());
    }

    @Test
    void testConvertCsvFileToXml_Success() throws IOException {
        String csvContent = "name,age\nBelkin Sergey,18\nKekishev Andrey,19";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        Path resultPath = conversionFrontendService.convertCsvFileToXml(mockFile, null);

        assertNotNull(resultPath);
        assertTrue(Files.exists(resultPath));

        String xmlContent = Files.readString(resultPath);

        assertTrue(xmlContent.contains("<name>Belkin Sergey</name>"));
        assertTrue(xmlContent.contains("<age>18</age>"));
        assertTrue(xmlContent.contains("<name>Kekishev Andrey</name>"));
        assertTrue(xmlContent.contains("<age>19</age>"));

        Files.deleteIfExists(resultPath);
    }

    @Test
    void testConvertCsvFileToXml_NoRows_ThrowsException() {
        String csvContent = "Олег,Монгол,Картофель,Стол";
        MockMultipartFile brokenFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionFrontendService.convertCsvFileToXml(brokenFile, null);
        });
        assertEquals("CSV file contains no rows to convert", exception.getMessage());
    }

    @Test
    void testConvertXmlFileToCsv_Success() throws IOException {
        String xmlContent = "<root>" +
                "<name>Belkin Sergey</name>" +
                "<age>18</age>" +
                "</root>";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.xml",
                "application/xml",
                xmlContent.getBytes()
        );

        Path resultPath = conversionFrontendService.convertXmlFileToCsv(mockFile, null);

        assertNotNull(resultPath);
        assertTrue(Files.exists(resultPath));

        String csvContent = Files.readString(resultPath);

        assertTrue(csvContent.contains("name"));
        assertTrue(csvContent.contains("age"));
        assertTrue(csvContent.contains("Belkin Sergey"));
        assertTrue(csvContent.contains("18"));

        Files.deleteIfExists(resultPath);
    }

    @Test
    void testConvertXmlFileToCsv_UnsupportedXMLStructure_ThrowsException() {
        String xmlContent = "<root>" +
                "<name.Сломанная структура.name>" +
                "<age.Тут должны быть проблемы.age>" +
                "</root>";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.xml",
                "application/xml",
                xmlContent.getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            conversionFrontendService.convertXmlFileToCsv(mockFile, null);
        });
    }

    @Test
    void testConvertXmlFileToCsv_NoRows_ThrowsException() {
        String xmlContent = "<root></root>";

        MockMultipartFile noRowsFile = new MockMultipartFile(
                "file",
                "test.xml",
                "application/xml",
                xmlContent.getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            conversionFrontendService.convertXmlFileToCsv(noRowsFile, null);
        });
    }

    @Test
    void testCountNumberOfOccurrences() {
        int count1 = ConversionFrontendService.countNumberOfOccurrences(".json", ".");
        assertEquals(1, count1);

        int count2 = ConversionFrontendService.countNumberOfOccurrences("my.bad.file.json", ".");
        assertEquals(3, count2);
    }
}