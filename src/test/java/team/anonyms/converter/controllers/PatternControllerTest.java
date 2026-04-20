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

import team.anonyms.converter.controllers.frontend.PatternController;
import team.anonyms.converter.controllers.frontend.pagination.PaginationHandler;
import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToCreateControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToUpdateControllerDto;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToUpdateServiceDto;
import team.anonyms.converter.mappers.PatternMapper;
import team.anonyms.converter.services.frontend.PatternService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
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

    @MockitoBean
    private PaginationHandler<PatternControllerDto> paginationHandler;

    @Test
    void testGetPatternsByUserId_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID patternId = UUID.randomUUID();

        PatternServiceDto serviceDto = new PatternServiceDto(
                patternId,
                "Test Pattern"
        );

        PatternControllerDto controllerDto = new PatternControllerDto(
                patternId,
                "Test Pattern"
        );

        Mockito.when(patternService.getAllPatternsByUserId(userId)).thenReturn(List.of(serviceDto));
        Mockito.when(patternMapper.patternServiceDtoToControllerDto(any(PatternServiceDto.class)))
                .thenReturn(controllerDto);

        Mockito.when(paginationHandler.makeSliceFromList(anyList(), anyInt(), anyInt()))
                .thenReturn(List.of(controllerDto));

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/patterns/%s/%d/%d", userId, 10, 0))
                        .contentType(MediaType.APPLICATION_JSON))
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
                List.of()
        );
        PatternToCreateServiceDto serviceRequestDto = new PatternToCreateServiceDto(
                userId,
                "New Pattern",
                List.of()
        );
        PatternServiceDto serviceResponseDto = new PatternServiceDto(
                patternId,
                "New Pattern"
        );
        PatternControllerDto responseDto = new PatternControllerDto(
                patternId,
                "New Pattern"
        );

        Mockito.when(patternMapper.patternToCreateControllerDtoToService(any(PatternToCreateControllerDto.class)))
                .thenReturn(serviceRequestDto);
        Mockito.when(patternService.createPattern(any(PatternToCreateServiceDto.class)))
                .thenReturn(serviceResponseDto);
        Mockito.when(patternMapper.patternServiceDtoToControllerDto(any(PatternServiceDto.class)))
                .thenReturn(responseDto);
        Mockito.when(paginationHandler.makeSliceFromList(List.of(responseDto), 1, 10)).thenReturn(List.of(responseDto));

        // 201 created
        mockMvc.perform(MockMvcRequestBuilders.post("/patterns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(patternId.toString()));
    }

    @Test
    void testUpdatePattern_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        PatternToUpdateControllerDto requestDto = new PatternToUpdateControllerDto(
                patternId,
                "Updated Pattern",
                List.of()
        );
        PatternToUpdateServiceDto serviceRequestDto = new PatternToUpdateServiceDto(
                patternId,
                "Updated Pattern",
                List.of()
        );
        PatternServiceDto serviceDto = new PatternServiceDto(
                patternId,
                "Updated Pattern"
        );
        PatternControllerDto responseDto = new PatternControllerDto(
                patternId,
                "Updated Pattern"
        );

        Mockito.when(patternMapper.patternToUpdateControllerDtoToService(any(PatternToUpdateControllerDto.class)))
                .thenReturn(serviceRequestDto);
        Mockito.when(patternService.updatePattern(any(PatternToUpdateServiceDto.class)))
                .thenReturn(serviceDto);
        Mockito.when(patternMapper.patternServiceDtoToControllerDto(any(PatternServiceDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/patterns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Pattern"));
    }

    @Test
    void testDeletePattern_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Mockito.doNothing().when(patternService).deletePattern(patternId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/patterns/" + patternId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}