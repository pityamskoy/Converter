package team.anonyms.converter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToCreateControllerDto;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.mappers.PatternMapper;
import team.anonyms.converter.services.PatternService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatternController.class)
@ContextConfiguration(classes = PatternController.class)
class PatternControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PatternService patternService;

    @MockitoBean
    private PatternMapper patternMapper;

    @Test
    void testGetAllPatternsByUserId_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID patternId = UUID.randomUUID();

        PatternServiceDto serviceDto = new PatternServiceDto(
                patternId,
                "Test Pattern",
                "json_csv",
                "test instruction",
                List.of()
        );

        PatternControllerDto controllerDto = new PatternControllerDto(
                patternId,
                "Test Pattern",
                "json_csv",
                "test instruction",
                List.of()
        );

        Mockito.when(patternService.getAllPatternsByUserId(userId)).thenReturn(List.of(serviceDto));
        Mockito.when(patternMapper.patternServiceDtoToControllerDto(any(PatternServiceDto.class)))
                .thenReturn(controllerDto);

        // в get передаем uuid, должен вернуться массив и первый элемент с тем же именем
        mockMvc.perform(MockMvcRequestBuilders.get("/patterns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Pattern"));
    }

    @Test
    void testCreatePattern_Success() throws Exception {
        UUID patternId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        PatternToCreateControllerDto requestDto = new PatternToCreateControllerDto(
                userId,
                "New Pattern",
                "json_csv",
                "test instruction",
                List.of()
        );

        PatternToCreateServiceDto serviceRequestDto = new PatternToCreateServiceDto(
                userId,
                "New Pattern",
                "json_csv",
                "test instruction",
                List.of()
        );

        PatternServiceDto serviceResponseDto = new PatternServiceDto(
                patternId,
                "New Pattern",
                "json_csv",
                "test instruction",
                List.of()
        );

        PatternControllerDto responseDto = new PatternControllerDto(
                patternId,
                "New Pattern",
                "json_csv",
                "test instruction",
                List.of()
        );

        Mockito.when(patternMapper.patternToCreateControllerDtoToService(any(PatternToCreateControllerDto.class)))
                .thenReturn(serviceRequestDto);
        Mockito.when(patternService.createPattern(any(PatternToCreateServiceDto.class)))
                .thenReturn(serviceResponseDto);
        Mockito.when(patternMapper.patternServiceDtoToControllerDto(any(PatternServiceDto.class)))
                .thenReturn(responseDto);

        // 201 created
        mockMvc.perform(MockMvcRequestBuilders.post("/patterns/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(patternId.toString()));
    }

    @Test
    void testDeletePattern_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        // void и возвращает 204
        Mockito.doNothing().when(patternService).deletePattern(patternId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/patterns/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patternId)))
                .andExpect(status().isNoContent());
    }
}