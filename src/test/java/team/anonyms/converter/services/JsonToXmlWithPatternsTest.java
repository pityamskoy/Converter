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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversionJsonToXmlTest {

    private PatternService patternService;
    private ConversionFrontendService conversionFrontendService;

    @BeforeEach
    void setUp() {
        patternService = Mockito.mock(PatternService.class);

        JsonMapper jsonMapper = new JsonMapper();
        XmlMapper xmlMapper = new XmlMapper();
        CsvMapper csvMapper = new CsvMapper();

        conversionFrontendService = new ConversionFrontendService(
                patternService, jsonMapper, xmlMapper, csvMapper
        );
    }

    @Test
    void testConvertJsonFileToXml_RenameFieldAndAssignValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        String inputJson = "[\n" +
                "  {\n" +
                "    \"field\": 12,\n" +
                "    \"modification\": null,\n" +
                "    \"name\": \"mishura\",\n" +
                "    \"age\": 45\n" +
                "  }\n" +
                "]";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                inputJson.getBytes()
        );

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
}