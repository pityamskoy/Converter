package team.anonyms.converter.services;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import team.anonyms.converter.errors.UnsupportedExtensionException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConversionServiceTest {

    private final ConversionService conversionService = new ConversionService();

    // тест из json в csv
    @Test
    void testConvertJsonFileToCsv_Success() throws IOException {
        // массив из 2 объектов
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

        // вызываем метод
        Path resultPath = conversionService.convertJsonFileToCsv(mockFile);

        // проверяем, что файл создался и все строки на месте как надо
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

    // из csv в json
    @Test
    void testConvertCsvFileToJson_Success() throws IOException {
        // в файле заголовок и 2 строчки
        String csvContent = "name,age\nIvan,30\nAnna,25";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        Path resultPath = conversionService.convertCsvFileToJson(mockFile);

        assertNotNull(resultPath);
        assertTrue(Files.exists(resultPath));

        String jsonContent = Files.readString(resultPath);

        // тут возвращается json-массив
        assertTrue(jsonContent.contains("\"name\":\"Ivan\""));
        assertTrue(jsonContent.contains("\"age\":30"));
        assertTrue(jsonContent.contains("\"name\":\"Anna\""));

        Files.deleteIfExists(resultPath);
    }

    @Test
    void testConvertJsonFileToCsv_EmptyFile_ThrowsException() {
        // пустой массив
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.json",
                "application/json",
                new byte[0]
        );

        // проверка на исключение
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionService.convertJsonFileToCsv(emptyFile);
        });
        assertEquals("jsonFile is empty", exception.getMessage());
    }

    @Test
    void testConvertCsvFileToJson_WrongExtension_ThrowsException() {
        // вместо csv тут расширение .txt
        MockMultipartFile txtFile = new MockMultipartFile(
                "file",
                "wrong.txt",
                "text/plain",
                "data".getBytes()
        );

        UnsupportedExtensionException exception = assertThrows(UnsupportedExtensionException.class, () -> {
            conversionService.convertCsvFileToJson(txtFile);
        });
        assertEquals("Provided file doesn't have '.csv' extension", exception.getMessage());
    }

    // UPD: тесты для конвертации json->xml, xml->json, csv->xml, xml->csv

    @Test
    void testConvertJsonFileToXml_Success() throws IOException {
        String jsonContent = "[{" +
                "\"name\":\"Belkin Sergey\",\"age\":18" +
                "}," +
                "{" +
                "\"name\":\"Krylov Daniil\",\"age\":19" +
                "}," +
                "{" +
                "\"name\":\"Tolstopyatov Trofim\",\"age\":20" +
                "}," +
                "{" +
                "\"name\":\"Kekishev Andrey\",\"age\":21" +
                "}]";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                jsonContent.getBytes()
        );

        Path resultPath = conversionService.convertJsonFileToXml(mockFile);

        assertNotNull(resultPath);
        assertTrue(Files.exists(resultPath));

        String xmlContent = Files.readString(resultPath);

        assertTrue(xmlContent.contains("<name>Belkin Sergey</name>"));
        assertTrue(xmlContent.contains("<age>18</age>"));
        assertTrue(xmlContent.contains("<name>Krylov Daniil</name>"));
        assertTrue(xmlContent.contains("<age>19</age>"));
        assertTrue(xmlContent.contains("<name>Kekishev Andrey</name>"));
        assertTrue(xmlContent.contains("<age>20</age>"));
        assertTrue(xmlContent.contains("<name>Tolstopyatov Trofim</name>"));
        assertTrue(xmlContent.contains("<age>21</age>"));

        Files.deleteIfExists(resultPath);
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

        Path resultPath = conversionService.convertXmlFileToJson(mockFile);

        assertNotNull(resultPath);
        assertTrue(Files.exists(resultPath));

        String jsonContent = Files.readString(resultPath);

        assertTrue(jsonContent.contains("\"name\":\"Kekishev Andrey\""));
        assertTrue(jsonContent.contains("\"age\":\"18\"") || jsonContent.contains("\"age\":18"));

        Files.deleteIfExists(resultPath);
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

        Path resultPath = conversionService.convertCsvFileToXml(mockFile);

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

        Path resultPath = conversionService.convertXmlFileToCsv(mockFile);

        assertNotNull(resultPath);
        assertTrue(Files.exists(resultPath));

        String csvContent = Files.readString(resultPath);

        assertTrue(csvContent.contains("name"));
        assertTrue(csvContent.contains("age"));
        assertTrue(csvContent.contains("Belkin Sergey"));
        assertTrue(csvContent.contains("18"));

        Files.deleteIfExists(resultPath);
    }

    // проверка статического метода
    @Test
    void testCountNumberOfOccurrences() {
        int count1 = ConversionService.countNumberOfOccurrences(".json", ".");
        assertEquals(1, count1);

        int count2 = ConversionService.countNumberOfOccurrences("my.bad.file.json", ".");
        assertEquals(3, count2);
    }
}