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
import team.anonyms.converter.dto.controller.user.UserControllerDto;
import team.anonyms.converter.dto.controller.user.UserToRegisterControllerDto;
import team.anonyms.converter.dto.service.credentials.CredentialsServiceDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.mappers.CredentialsMapper;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.services.frontend.AuthenticationService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@ContextConfiguration(classes = AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // заглушки
    @MockitoBean
    private AuthenticationService authenticationService;
    @MockitoBean
    private CredentialsMapper credentialsMapper;
    @MockitoBean
    private UserMapper userMapper;

    @Test
    void testLogout_Success() throws Exception {
        String userId = "test-user-123";
        // фейк кукисы
        Cookie expectedCookie = new Cookie("user_id", "");
        expectedCookie.setMaxAge(0);

        Mockito.when(authenticationService.logout(userId)).thenReturn(expectedCookie);

        // проверка на 204 и что везде пусто
        mockMvc.perform(MockMvcRequestBuilders.delete("/auth")
                        .cookie(new Cookie("user_id", userId)))
                .andExpect(status().isNoContent())
                .andExpect(cookie().exists("user_id"))
                .andExpect(cookie().maxAge("user_id", 0));
    }

    @Test
    void testRegister_Success() throws Exception {
        UUID fakeId = UUID.randomUUID();

        // пустые дтошки
        UserToRegisterControllerDto requestDto = new UserToRegisterControllerDto(
                "testuser",
                "test@mail.com",
                "password123"
        );

        UserToRegisterServiceDto mockRegisterServiceDto =
                new team.anonyms.converter.dto.service.user.UserToRegisterServiceDto(
                        "testuser",
                        "test@mail.com",
                        "password123"
                );

        UserServiceDto mockServiceDto = new UserServiceDto(
                fakeId,
                "testuser",
                "test@mail.com",
                List.of()
        );

        UserControllerDto responseDto = new UserControllerDto(
                fakeId,
                "testuser",
                "test@mail.com",
                List.of()
        );

        Cookie mockCookie = new Cookie("user_id", "new-user-123");
        Pair<UserServiceDto, Cookie> serviceResult = new Pair<>(mockServiceDto, mockCookie);

        Mockito.when(userMapper.userToRegisterControllerDtoToService(any(UserToRegisterControllerDto.class)))
                .thenReturn(mockRegisterServiceDto);

        Mockito.when(authenticationService.register(any(UserToRegisterServiceDto.class)))
                .thenReturn(serviceResult);

        Mockito.when(userMapper.userServiceDtoToControllerDto(any(UserServiceDto.class)))
                .thenReturn(responseDto);

        // проверка имени и прочего
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(cookie().value("user_id", "new-user-123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser"));
    }
    @Test
    void testLogin_Success() throws Exception {
        String existingUserId = "old-session-123";
        UUID fakeTokenId = UUID.randomUUID();
        String fakeUsername = "fakeUsername";
        String fakeEmail = "fakeEmail";

        CredentialsControllerDto requestDto = new CredentialsControllerDto(
                "testuser",
                "password123"
        );

        CredentialsServiceDto mockServiceCredentials = new CredentialsServiceDto(
                        "testuser",
                        "password123"
        );

        LoginResultServiceDto mockServiceResult = new LoginResultServiceDto(
                true,
                fakeUsername,
                fakeEmail,
                fakeTokenId
        );
        LoginResultControllerDto responseDto = new LoginResultControllerDto(
                true,
                fakeUsername,
                fakeEmail,
                fakeTokenId
        );

        // кукис
        Cookie newSessionCookie = new Cookie("user_id", "new-session-456");
        Pair<Cookie, LoginResultServiceDto> serviceResult = new Pair<>(newSessionCookie, mockServiceResult);

        Mockito.when(credentialsMapper.credentialsControllerDtoToService(any(CredentialsControllerDto.class)))
                .thenReturn(mockServiceCredentials);

        Mockito.when(authenticationService.login(Mockito.eq(existingUserId), any(CredentialsServiceDto.class)))
                .thenReturn(serviceResult);

        Mockito.when(credentialsMapper.loginResultServiceDtoToController(any(LoginResultServiceDto.class)))
                .thenReturn(responseDto);

        // передаем старый кукис, ждем, что все ок и вернется новый
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .cookie(new Cookie("user_id", existingUserId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(cookie().value("user_id", "new-session-456"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
    }

    @Test
    void testLogin_BadCredentials() throws Exception {
        CredentialsControllerDto requestDto = new CredentialsControllerDto("wronguser", "wrongpass");
        CredentialsServiceDto mockServiceCredentials =
                new CredentialsServiceDto("wronguser", "wrongpass");

        Mockito.when(credentialsMapper.credentialsControllerDtoToService(any(CredentialsControllerDto.class)))
                .thenReturn(mockServiceCredentials);

        // эмулируем выброс исключения
        Mockito.when(authenticationService.login(any(), any(CredentialsServiceDto.class)))
                .thenThrow(new javax.security.auth.login.CredentialException("Invalid credentials"));

        //все плохо и ошибка 400
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}