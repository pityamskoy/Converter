package team.anonyms.converter.services;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import team.anonyms.converter.errors.UnsupportedExtensionException;
import team.anonyms.converter.services.frontend.ConversionFrontendService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConversionFrontendServiceTest {

    private final ConversionFrontendService conversionFrontendService = new ConversionFrontendService();

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
        Path resultPath = conversionFrontendService.convertJsonFileToCsv(mockFile);

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

        Path resultPath = conversionFrontendService.convertCsvFileToJson(mockFile);

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
            conversionFrontendService.convertJsonFileToCsv(emptyFile);
        });
        assertEquals("file is empty", exception.getMessage());
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
            conversionFrontendService.convertCsvFileToJson(txtFile);
        });
        assertEquals("Provided file doesn't have '.csv' extension", exception.getMessage());
    }

    // проверка статического метода
    @Test
    void testCountNumberOfOccurrences() {
        int count1 = ConversionFrontendService.countNumberOfOccurrences(".json", ".");
        assertEquals(1, count1);

        int count2 = ConversionFrontendService.countNumberOfOccurrences("my.bad.file.json", ".");
        assertEquals(3, count2);
    }
}