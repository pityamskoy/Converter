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

import team.anonyms.converter.controllers.frontend.UserController;
import team.anonyms.converter.dto.controller.user.UserControllerDto;
import team.anonyms.converter.dto.controller.user.UserToUpdateControllerDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateServiceDto;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.services.frontend.UserService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserMapper userMapper;

    @Test
    void testUpdateUser_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        UserToUpdateControllerDto requestDto = new UserToUpdateControllerDto(
                userId,
                "newname",
                "new@gmail.com",
                "test password",
                List.of()
        );

        UserToUpdateServiceDto serviceRequestDto = new UserToUpdateServiceDto(
                        userId,
                "newname",
                "new@gmail.com",
                "test password",
                List.of()
        );

        UserServiceDto serviceResponseDto = new UserServiceDto(
                userId,
                "newname",
                "new@gmail.com",
                List.of()
        );

        UserControllerDto responseDto = new UserControllerDto(
                userId,
                "newname",
                "new@gmail.com",
                List.of()
        );

        Mockito.when(userMapper.userToUpdateControllerDtoToService(any(UserToUpdateControllerDto.class)))
                .thenReturn(serviceRequestDto);
        Mockito.when(userService.updateUser(any(team.anonyms.converter.dto.service.user.UserToUpdateServiceDto.class)))
                .thenReturn(serviceResponseDto);
        Mockito.when(userMapper.userServiceDtoToControllerDto(any(UserServiceDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        // 204
        Mockito.doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + userId))
                .andExpect(status().isNoContent());
    }
}