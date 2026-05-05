package team.anonyms.converter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import team.anonyms.converter.controllers.frontend.UserController;
import team.anonyms.converter.dto.controller.credentials.LoginResultControllerDto;
import team.anonyms.converter.dto.controller.user.UserControllerDto;
import team.anonyms.converter.dto.controller.user.UserToRegisterControllerDto;
import team.anonyms.converter.dto.controller.user.UserToUpdateControllerDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateServiceDto;
import team.anonyms.converter.mappers.CredentialsMapper;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.services.frontend.UserService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private CredentialsMapper credentialsMapper;

    @Test
    void testRegister_Success() throws Exception {
        UUID fakeId = UUID.randomUUID();
        String generatedToken = "new.jwt.token";

        UserToRegisterControllerDto requestDto = new UserToRegisterControllerDto(
                "testuser",
                "test@mail.com",
                "password123"
        );

        UserToRegisterServiceDto mockRegisterServiceDto = new UserToRegisterServiceDto(
                "testuser",
                "test@mail.com",
                "password123"
        );

        LoginResultServiceDto mockServiceResult = new LoginResultServiceDto(
                true, "testuser", "test@mail.com", fakeId
        );

        LoginResultControllerDto responseDto = new LoginResultControllerDto(
                true, "testuser", "test@mail.com", fakeId
        );

        Mockito.when(userMapper.userToRegisterControllerDtoToService(any(UserToRegisterControllerDto.class)))
                .thenReturn(mockRegisterServiceDto);

        Mockito.when(userService.register(any(UserToRegisterServiceDto.class)))
                .thenReturn(new Pair<>(mockServiceResult, generatedToken));

        Mockito.when(credentialsMapper.loginResultServiceDtoToController(any(LoginResultServiceDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser"))
                .andExpect(cookie().value("jwtToken", generatedToken))
                .andExpect(cookie().httpOnly("jwtToken", true))
                .andExpect(cookie().secure("jwtToken", true));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        UserToUpdateControllerDto requestDto = new UserToUpdateControllerDto(
                userId,
                "newname",
                "new@gmail.com",
                "test password"
        );

        UserToUpdateServiceDto serviceRequestDto = new UserToUpdateServiceDto(
                userId,
                "newname",
                "new@gmail.com",
                "test password"
        );

        UserServiceDto serviceResponseDto = new UserServiceDto(
                userId,
                "newname",
                "new@gmail.com",
                true
        );

        UserControllerDto responseDto = new UserControllerDto(
                userId,
                "newname",
                "new@gmail.com",
                true
        );

        Mockito.when(userMapper.userToUpdateControllerDtoToService(any(UserToUpdateControllerDto.class)))
                .thenReturn(serviceRequestDto);

        Mockito.when(userService.updateUser(any(UserToUpdateServiceDto.class)))
                .thenReturn(serviceResponseDto);

        Mockito.when(userMapper.userServiceDtoToControllerDto(any(UserServiceDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("newname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isVerified").value(true));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        Mockito.doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + userId))
                .andExpect(status().isNoContent());
    }
}