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

        Mockito.when(conversionFrontendService.convertJsonFileToCsv(any())).thenReturn(tempCsvFile);

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
        org.springframework.mock.web.MockMultipartFile mockFile = new org.springframework.mock.web.MockMultipartFile(
                "file",
                "data.csv",
                "text/csv",
                "key,value\nname,test".getBytes()
        );

        java.nio.file.Path tempJsonFile = java.nio.file.Files.createTempFile("converted", ".json");
        java.nio.file.Files.write(tempJsonFile, "{\"name\":\"test\"}".getBytes());

        Mockito.when(conversionFrontendService.convertCsvFileToJson(any())).thenReturn(tempJsonFile);

        org.springframework.test.web.servlet.MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/conversion/csv/json")
                        .file(mockFile))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"data.json\""))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string("{\"name\":\"test\"}"));
    }
}