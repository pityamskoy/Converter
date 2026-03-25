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

import team.anonyms.converter.controllers.frontend.ModificationController;
import team.anonyms.converter.dto.controller.modification.ModificationControllerDto;
import team.anonyms.converter.dto.controller.modification.ModificationToCreateControllerDto;
import team.anonyms.converter.dto.service.modification.ModificationServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;
import team.anonyms.converter.mappers.ModificationMapper;
import team.anonyms.converter.services.frontend.ModificationService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificationController.class)
@ContextConfiguration(classes = ModificationController.class)
class ModificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ModificationService modificationService;
    @MockitoBean
    private ModificationMapper modificationMapper;

    @Test
    void testGetAllModificationsByPatternId_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        ModificationServiceDto serviceDto = new ModificationServiceDto(
                patternId,
                "old name",
                "new name",
                "json",
                "Test value 21.03"
        );

        ModificationControllerDto controllerDto = new ModificationControllerDto(
                patternId,
                "old name",
                "new name",
                "json",
                "Test value 21.03"
        );

        Mockito.when(modificationService.getAllModificationsByPatternId(patternId)).thenReturn(List.of(serviceDto));
        Mockito.when(modificationMapper.modificationServiceDtoToControllerDto(any(ModificationServiceDto.class)))
                .thenReturn(controllerDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/modifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patternId)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateModification_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        ModificationToCreateControllerDto requestDto = new ModificationToCreateControllerDto(
                patternId,
                "old name",
                "new name",
                "json",
                "Test value 21.03"
        );

        ModificationToCreateServiceDto serviceRequestDto = new ModificationToCreateServiceDto(
                patternId,
                "old name",
                "new name",
                "json",
                "Test value 21.03"
        );

        ModificationServiceDto serviceResponseDto = new ModificationServiceDto(
                patternId,
                "old name",
                "new name",
                "json",
                "Test value 21.03"
        );

        ModificationControllerDto responseDto = new ModificationControllerDto(
                patternId,
                "old name",
                "new name",
                "json",
                "Test value 21.03"
        );

        Mockito.when(modificationMapper.modificationToCreateControllerDtoToService(any(ModificationToCreateControllerDto.class)))
                .thenReturn(serviceRequestDto);
        Mockito.when(modificationService.createModification(any(ModificationToCreateServiceDto.class)))
                .thenReturn(serviceResponseDto);
        Mockito.when(modificationMapper.modificationServiceDtoToControllerDto(any(ModificationServiceDto.class)))
                .thenReturn(responseDto);

        // 201
        mockMvc.perform(MockMvcRequestBuilders.post("/modifications/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateModification_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        ModificationControllerDto requestDto = new ModificationControllerDto(
                patternId,
                "old name",
                "new name",
                "json",
                "Test value 21.03"
        );

        ModificationServiceDto serviceRequestDto = new ModificationServiceDto(
                patternId,
                "old name",
                "new name",
                "json",
                "Test value 21.03"
        );
        ModificationServiceDto serviceResponseDto = new ModificationServiceDto(
                patternId,
                "old name",
                "new name",
                "json",
                "Test value 21.03"
        );

        ModificationControllerDto responseDto = new ModificationControllerDto(
                patternId,
                "old name",
                "new name",
                "json",
                "Test value 21.03"
        );

        Mockito.when(modificationMapper.modificationControllerDtoToServiceDto(any(ModificationControllerDto.class)))
                .thenReturn(serviceRequestDto);
        Mockito.when(modificationService.updateModification(any(ModificationServiceDto.class)))
                .thenReturn(serviceResponseDto);
        Mockito.when(modificationMapper.modificationServiceDtoToControllerDto(any(ModificationServiceDto.class)))
                .thenReturn(responseDto);


        mockMvc.perform(MockMvcRequestBuilders.put("/modifications/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteModification_Success() throws Exception {
        UUID modificationId = UUID.randomUUID();

        // 204
        Mockito.doNothing().when(modificationService).deleteModification(modificationId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/modifications/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modificationId)))
                .andExpect(status().isNoContent());
    }
}