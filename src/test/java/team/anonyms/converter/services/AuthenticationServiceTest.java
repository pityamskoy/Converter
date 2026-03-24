package team.anonyms.converter.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import team.anonyms.converter.dto.service.credentials.CredentialsServiceDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.repositories.UserRepository;

import javax.security.auth.login.CredentialException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationService authenticationService;

    // нет почты-пароля и кукисов
    @Test
    void testLogin_MissingCredentials_ThrowsCredentialException() {
        CredentialsServiceDto emptyCredentials = new CredentialsServiceDto(null, null);

        assertThrows(CredentialException.class, () -> {
            authenticationService.login(null, emptyCredentials);
        });
    }

    // логин с несуществующей почтой
    @Test
    void testLogin_NonexistentEmail_ThrowsEntityNotFoundException() {
        String email = "test@gmail.com";
        String password = "test_password";
        CredentialsServiceDto credentials = new CredentialsServiceDto(email, password);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.login(null, credentials);
        });

        assertTrue(exception.getMessage().contains("User not found; email=" + email));
    }

    // логин по кукам с несуществующим айди
    @Test
    void testLogin_NonexistentId_ThrowsEntityNotFoundException() {
        String fakeId = UUID.randomUUID().toString();
        CredentialsServiceDto emptyCredentials = new CredentialsServiceDto(null, null);

        Mockito.when(userRepository.findById(UUID.fromString(fakeId))).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.login(fakeId, emptyCredentials);
        });

        assertTrue(exception.getMessage().contains("User not found; id=" + fakeId));
    }

    // есть куки и пустые креды
    @Test
    void testLogin_WithUserIdCookie_Success() throws CredentialException {
        UUID userId = UUID.randomUUID();
        CredentialsServiceDto emptyCredentials = new CredentialsServiceDto(null, null);
        User mockUser = new User();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        Pair<Cookie, LoginResultServiceDto> result = authenticationService.login(userId.toString(), emptyCredentials);

        assertNull(result.a);
        assertTrue(result.b.success());
        assertEquals(userId, result.b.userId());
    }

    // вход по почте, возвращаем успех и куки
    @Test
    void testLogin_WithEmail_CorrectPassword() throws CredentialException {
        String email = "test@gmail.com";
        String password = "correct_password";
        UUID userId = UUID.randomUUID();
        CredentialsServiceDto credentials = new CredentialsServiceDto(email, password);

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setPassword(password);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        Pair<Cookie, LoginResultServiceDto> result = authenticationService.login(null, credentials);

        assertNotNull(result.a);
        assertEquals("user_id", result.a.getName());
        assertTrue(result.b.success());
    }

    // вход с неверным паролем, куки быть не должно и статус false
    @Test
    void testLogin_WithEmail_WrongPassword() throws CredentialException {
        String email = "test@gmail.com";
        CredentialsServiceDto credentials = new CredentialsServiceDto(email, "wrong_password");

        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setPassword("correct_password");

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        Pair<Cookie, LoginResultServiceDto> result = authenticationService.login(null, credentials);

        assertNull(result.a);
        assertFalse(result.b.success());
    }

    // регистрация с именем, почтой и паролем
    @Test
    void testRegister_Success() {
        UserToRegisterServiceDto registerDto = new UserToRegisterServiceDto(
                "user",
                "test@gmail.com",
                "pass"
        );
        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());

        UserServiceDto responseDto = new UserServiceDto(
                mockUser.getId(),
                "user",
                "test@gmail.com",
                List.of()
        );

        Mockito.when(userMapper.userToRegisterServiceDtoToEntity(registerDto)).thenReturn(mockUser);
        Mockito.when(userMapper.userToServiceDto(mockUser)).thenReturn(responseDto);

        Pair<UserServiceDto, Cookie> result = authenticationService.register(registerDto);

        assertNotNull(result.a);
        assertNotNull(result.b);
        assertEquals("user_id", result.b.getName());
        assertEquals(14400, result.b.getMaxAge());
        Mockito.verify(userRepository).save(mockUser);
    }

    // очистка куки после выхода из аккаунта
    @Test
    void testLogout_Success() {
        String userId = "test-123";
        Cookie cookie = authenticationService.logout(userId);

        assertNotNull(cookie);
        assertEquals("user_id", cookie.getName());
        assertEquals(userId, cookie.getValue());
        assertEquals(0, cookie.getMaxAge());
    }
}