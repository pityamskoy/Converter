package team.anonyms.converter.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
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

    // ???
    // Expected: class java.lang.IllegalArgumentException
    // Actual: class com.fasterxml.jackson.core.JsonParseException
    @Test
    void testConvertJsonFileToCsv_UnsupportedJSONStructure_ThrowsException() {
        String jsonContent = "[{" +
                "\"name\":\"Муся\"\"age\":30000" + // пропущена запятая
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

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionService.convertJsonFileToCsv(brokenFile);
        });
    }

    // ???
    // Expected: class java.lang.IllegalArgumentException
    // Actual: class com.fasterxml.jackson.dataformat.csv.CsvWriteException
    @Test
    void testConvertJsonFileToCsv_NoRows_ThrowsException() {
        String jsonContent = "[{}]";

        MockMultipartFile brokenFile = new MockMultipartFile(
                "file",
                "test_file.json",
                "application/json",
                jsonContent.getBytes()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionService.convertJsonFileToCsv(brokenFile);
        });
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

    // а вот тут все хорошо, в отличие от json->csv
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
            conversionService.convertCsvFileToJson(brokenFile);
        });
        assertEquals("CSV file contains no rows to convert", exception.getMessage());
    }

    // ненужные тесты удалены, остались три переделанных под метод валидации
    // метод приватный, поэтому тестирую через json->csv
    @Test
    void testValidateArgumentsForConversion_EmptyFile_ThrowsException() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.json",
                "application/json",
                new byte[0]
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionService.convertJsonFileToCsv(emptyFile);
        });
        assertEquals("file is empty", exception.getMessage());
    }

    // грозный хардкод null-имени, ибо MockMultipartFile мне не разрешает(((
    @Test
    void testValidateArgumentsForConversion_NullFilename_ThrowsException() {
        MultipartFile brokenFile = Mockito.mock(MultipartFile.class);

        Mockito.when(brokenFile.isEmpty()).thenReturn(false);
        Mockito.when(brokenFile.getOriginalFilename()).thenReturn(null);

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            conversionService.convertJsonFileToCsv(brokenFile);
        });
        assertEquals("filename is null", exception.getMessage());
    }

    // не то расширение
    @Test
    void testValidateArgumentsForConversion_WrongExtension_ThrowsException() {
        // вместо json тут расширение .txt
        MockMultipartFile txtFile = new MockMultipartFile(
                "file",
                "wrong.txt",
                "text/plain",
                "data".getBytes()
        );

        UnsupportedExtensionException exception = assertThrows(UnsupportedExtensionException.class, () -> {
            conversionService.convertJsonFileToCsv(txtFile);
        });

        assertEquals("Provided file doesn't have '.json' extension", exception.getMessage());
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

    // ???
    // Expected: class java.lang.IllegalArgumentException
    // Actual: class com.fasterxml.jackson.core.JsonParseException
    @Test
    void testConvertJsonFileToXml_UnsupportedJSONStructure_ThrowsException() {
        String jsonContent = "[{" +
                "\"name\":\"Муся\"\"age\":30000" + // пропущена запятая
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

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionService.convertJsonFileToCsv(brokenFile);
        });
    }

    // No rows exception doesn't happen at all.
    // Maybe it is possible to find a jsonContent to make this exception to be thrown
    @Test
    void testConvertJsonFileToXml_NoRows_ThrowsException() {
        String jsonContent = "[{}]";

        MockMultipartFile brokenFile = new MockMultipartFile(
                "file",
                "test_file.json",
                "application/json",
                jsonContent.getBytes()
        );
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

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionService.convertXmlFileToJson(mockFile);
        });
    }

    // Either it always converts correctly or try to find out new breaking XML content.
    @Test
    void testConvertXmlFileToJson_NoRows_ThrowsException() {
        String xmlContent = "<root>" +
                "</root>";

        MockMultipartFile noRowsFile = new MockMultipartFile(
                "file",
                "test.xml",
                "application/xml",
                xmlContent.getBytes()
        );
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

    // тут все нормально, модно, молодежно
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
            conversionService.convertCsvFileToXml(brokenFile);
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

    // ???
    // Expected: class java.lang.IllegalArgumentException
    // Actual: class com.fasterxml.jackson.core.JsonParseException
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

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionService.convertXmlFileToCsv(mockFile);
        });
    }

    // тут то же самое подробнее: надо java.lang.IllegalArgumentException (как в сервисе),
    // по итогу наша ошибка не выскакивает, вместо нее ошибка библиотеки
    // com.fasterxml.jackson.dataformat.csv.CsvWriteException:
    // Schema specified that header line is to be written; but contains no column names
    @Test
    void testConvertXmlFileToCsv_NoRows_ThrowsException() {
        String xmlContent = "<root>" +
                "</root>";

        MockMultipartFile noRowsFile = new MockMultipartFile(
                "file",
                "test.xml",
                "application/xml",
                xmlContent.getBytes()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            conversionService.convertXmlFileToCsv(noRowsFile);
        });
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