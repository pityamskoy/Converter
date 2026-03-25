package team.anonyms.converter.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import team.anonyms.converter.controllers.frontend.ConversionFrontendController;
import team.anonyms.converter.services.frontend.ConversionFrontendService;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(ConversionFrontendController.class)
@ContextConfiguration(classes = ConversionFrontendController.class)
class ConversionFrontendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversionFrontendService conversionFrontendService;

    @Test
    void testConvertJsonFileToCsv_Success() throws Exception {

        // имитация файла
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-data.json",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"key\": \"value\"}".getBytes()
        );

        // реальный файл .csv
        Path tempCsvFile = Files.createTempFile("converted", ".csv");
        Files.write(tempCsvFile, "key,value".getBytes());

        Mockito.when(conversionFrontendService.convertJsonFileToCsv(any(), null)).thenReturn(tempCsvFile);

        // post-запрос
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/conversion/json/csv")
                        .file(mockFile))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        // проверка, что все норм и код 200
        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"test-data.csv\""))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("text/csv"))
                .andExpect(MockMvcResultMatchers.content().string("key,value"));
    }

    @Test
    void testConvertCsvFileToJson_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "data.csv",
                "text/csv",
                "key,value\nname,test".getBytes()
        );

        Path tempJsonFile = Files.createTempFile("converted", ".json");
        Files.write(tempJsonFile, "{\"name\":\"test\"}".getBytes());

        Mockito.when(conversionFrontendService.convertCsvFileToJson(any(), null)).thenReturn(tempJsonFile);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/conversion/csv/json")
                        .file(mockFile))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"data.json\""))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(MockMvcResultMatchers.content().string("{\"name\":\"test\"}"));
    }

    // UPD: тесты для логики, связанной с xml

    @Test
    void testConvertJsonFileToXml_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test_data.json",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"test_key\":\"test_value\"}".getBytes()
        );

        Path tempXmlFile = Files.createTempFile("converted", ".xml");
        Files.write(tempXmlFile, "<name>test</name>".getBytes());

        Mockito.when(conversionFrontendService.convertJsonFileToXml(any(), null)).thenReturn(tempXmlFile);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/conversion/json/xml")
                        .file(mockFile))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"test_data.xml\""))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(MockMvcResultMatchers.content().string("<name>test</name>"));
    }

    @Test
    void testConvertXmlFileToJson_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test_data.xml",
                MediaType.APPLICATION_XML_VALUE,
                "<name>test</name>".getBytes()
        );

        Path tempJsonFile = Files.createTempFile("converted", ".json");
        Files.write(tempJsonFile, "{\"name\":\"test\"}".getBytes());

        Mockito.when(conversionFrontendService.convertXmlFileToJson(any(), null)).thenReturn(tempJsonFile);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/conversion/xml/json")
                        .file(mockFile))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"test_data.json\""))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(MockMvcResultMatchers.content().string("{\"name\":\"test\"}"));
    }

    @Test
    void testConvertXmlFileToCsv_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test_data.xml",
                MediaType.APPLICATION_XML_VALUE,
                "<name>test</name>".getBytes()
        );

        Path tempCsvFile = Files.createTempFile("converted", ".csv");
        Files.write(tempCsvFile, "name\ntest".getBytes());

        Mockito.when(conversionFrontendService.convertXmlFileToCsv(any(), null)).thenReturn(tempCsvFile);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/conversion/xml/csv")
                        .file(mockFile))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"test_data.csv\""))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("text/csv"))
                .andExpect(MockMvcResultMatchers.content().string("name\ntest"));
    }

    @Test
    void testConvertCsvFileToXml_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test_data.csv",
                "text/csv",
                "name\ntest".getBytes()
        );

        Path tempXmlFile = Files.createTempFile("converted", ".xml");
        Files.write(tempXmlFile, "<name>test</name>".getBytes());

        Mockito.when(conversionFrontendService.convertCsvFileToXml(any(), null)).thenReturn(tempXmlFile);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/conversion/csv/xml")
                        .file(mockFile))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"test_data.xml\""))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(MockMvcResultMatchers.content().string("<name>test</name>"));
    }
}