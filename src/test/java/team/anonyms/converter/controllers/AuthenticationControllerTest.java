package team.anonyms.converter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.antlr.v4.runtime.misc.Pair;
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
import team.anonyms.converter.dto.service.credentials.CredentialsServiceDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.mappers.CredentialsMapper;
import team.anonyms.converter.services.frontend.AuthenticationService;
import team.anonyms.converter.services.frontend.EmailService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@ContextConfiguration(classes = {AuthenticationController.class, GlobalExceptionHandler.class})
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationService authenticationService;
    @MockitoBean
    private EmailService emailService;
    @MockitoBean
    private CredentialsMapper credentialsMapper;

    @Test
    void testLogin_Success() throws Exception {
        UUID fakeUserId = UUID.randomUUID();
        String fakeUsername = "testuser";
        String fakeEmail = "test@mail.com";
        String generatedToken = "valid.jwt.token";

        CredentialsControllerDto requestDto = new CredentialsControllerDto(
                fakeEmail, "password123"
        );

        CredentialsServiceDto mockServiceCredentials = new CredentialsServiceDto(
                fakeEmail, "password123"
        );

        LoginResultServiceDto mockServiceResult = new LoginResultServiceDto(
                true, fakeUsername, fakeEmail, fakeUserId
        );

        LoginResultControllerDto responseDto = new LoginResultControllerDto(
                true, fakeUsername, fakeEmail, fakeUserId
        );

        Mockito.when(credentialsMapper.credentialsControllerDtoToService(any(CredentialsControllerDto.class)))
                .thenReturn(mockServiceCredentials);

        Mockito.when(authenticationService.login(any(CredentialsServiceDto.class), eq(null)))
                .thenReturn(new Pair<>(mockServiceResult, generatedToken));

        Mockito.when(credentialsMapper.loginResultServiceDtoToController(any(LoginResultServiceDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(fakeUsername))
                .andExpect(cookie().value("jwtToken", generatedToken))
                .andExpect(cookie().httpOnly("jwtToken", true))
                .andExpect(cookie().secure("jwtToken", true));
    }

    @Test
    void testLogin_WithExistingCookie_Success() throws Exception {
        UUID fakeUserId = UUID.randomUUID();
        String fakeUsername = "testuser";
        String fakeEmail = "test@mail.com";
        String existingToken = "existing.jwt.token";

        CredentialsControllerDto requestDto = new CredentialsControllerDto(null, null);
        CredentialsServiceDto mockServiceCredentials = new CredentialsServiceDto(null, null);

        LoginResultServiceDto mockServiceResult = new LoginResultServiceDto(
                true, fakeUsername, fakeEmail, fakeUserId
        );

        LoginResultControllerDto responseDto = new LoginResultControllerDto(
                true, fakeUsername, fakeEmail, fakeUserId
        );

        Mockito.when(credentialsMapper.credentialsControllerDtoToService(any(CredentialsControllerDto.class)))
                .thenReturn(mockServiceCredentials);

        Mockito.when(authenticationService.login(any(CredentialsServiceDto.class), eq(existingToken)))
                .thenReturn(new Pair<>(mockServiceResult, existingToken));

        Mockito.when(credentialsMapper.loginResultServiceDtoToController(any(LoginResultServiceDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                        .cookie(new Cookie("jwtToken", existingToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(cookie().value("jwtToken", existingToken));
    }

    @Test
    void testLogin_BadCredentials() throws Exception {
        CredentialsControllerDto requestDto = new CredentialsControllerDto("wrong@mail.com", "wrongpass");
        CredentialsServiceDto mockServiceCredentials = new CredentialsServiceDto("wrong@mail.com", "wrongpass");

        Mockito.when(credentialsMapper.credentialsControllerDtoToService(any(CredentialsControllerDto.class)))
                .thenReturn(mockServiceCredentials);

        Mockito.when(authenticationService.login(any(CredentialsServiceDto.class), eq(null)))
                .thenThrow(new javax.security.auth.login.CredentialException("Invalid credentials"));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogout_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/auth"))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("jwtToken", 0))
                .andExpect(cookie().value("jwtToken", (String) null));
    }
}