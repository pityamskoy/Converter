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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import team.anonyms.converter.controllers.frontend.AuthenticationController;
import team.anonyms.converter.dto.controller.credentials.CredentialsControllerDto;
import team.anonyms.converter.dto.controller.credentials.LoginResultControllerDto;
import team.anonyms.converter.dto.controller.user.UserToRegisterControllerDto;
import team.anonyms.converter.dto.service.credentials.CredentialsServiceDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.mappers.CredentialsMapper;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.services.frontend.AuthenticationService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/*
@WebMvcTest(AuthenticationController.class)
@ContextConfiguration(classes = {AuthenticationController.class, GlobalExceptionHandler.class})
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationService authenticationService;
    @MockitoBean
    private CredentialsMapper credentialsMapper;
    @MockitoBean
    private UserMapper userMapper;

    @Test
    void testRegister_Success() throws Exception {
        UUID fakeId = UUID.randomUUID();
        String fakeToken = "new.jwt.token";

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
                true, "testuser", "test@mail.com", fakeId, fakeToken
        );

        LoginResultControllerDto responseDto = new LoginResultControllerDto(
                true, "testuser", "test@mail.com", fakeId, fakeToken
        );

        Mockito.when(userMapper.userToRegisterControllerDtoToService(any(UserToRegisterControllerDto.class)))
                .thenReturn(mockRegisterServiceDto);

        Mockito.when(authenticationService.register(any(UserToRegisterServiceDto.class)))
                .thenReturn(mockServiceResult);

        Mockito.when(credentialsMapper.loginResultServiceDtoToController(any(LoginResultServiceDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwtToken").value(fakeToken)); // Проверяем токен!
    }

    @Test
    void testLogin_Success() throws Exception {
        UUID fakeUserId = UUID.randomUUID();
        String fakeUsername = "testuser";
        String fakeEmail = "test@mail.com";
        String fakeToken = "valid.jwt.token";

        CredentialsControllerDto requestDto = new CredentialsControllerDto(
                fakeEmail, "password123", null
        );

        CredentialsServiceDto mockServiceCredentials = new CredentialsServiceDto(
                fakeEmail, "password123", null
        );

        LoginResultServiceDto mockServiceResult = new LoginResultServiceDto(
                true, fakeUsername, fakeEmail, fakeUserId, fakeToken
        );

        LoginResultControllerDto responseDto = new LoginResultControllerDto(
                true, fakeUsername, fakeEmail, fakeUserId, fakeToken
        );

        Mockito.when(credentialsMapper.credentialsControllerDtoToService(any(CredentialsControllerDto.class)))
                .thenReturn(mockServiceCredentials);

        Mockito.when(authenticationService.login(any(CredentialsServiceDto.class)))
                .thenReturn(mockServiceResult);

        Mockito.when(credentialsMapper.loginResultServiceDtoToController(any(LoginResultServiceDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwtToken").value(fakeToken));
    }

    @Test
    void testLogin_BadCredentials() throws Exception {
        CredentialsControllerDto requestDto = new CredentialsControllerDto("wrong@mail.com", "wrongpass", null);
        CredentialsServiceDto mockServiceCredentials = new CredentialsServiceDto("wrong@mail.com", "wrongpass", null);

        Mockito.when(credentialsMapper.credentialsControllerDtoToService(any(CredentialsControllerDto.class)))
                .thenReturn(mockServiceCredentials);

        Mockito.when(authenticationService.login(any(CredentialsServiceDto.class)))
                .thenThrow(new javax.security.auth.login.CredentialException("Invalid credentials"));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}*/