package team.anonyms.converter.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import team.anonyms.converter.dto.service.credentials.CredentialsServiceDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.services.frontend.AuthenticationService;
import team.anonyms.converter.services.frontend.JwtService;

import javax.security.auth.login.CredentialException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
/*
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void testLogin_MissingCredentials_ThrowsCredentialException() {
        CredentialsServiceDto emptyCredentials = new CredentialsServiceDto(null, null, null);

        assertThrows(CredentialException.class, () -> {
            authenticationService.login(emptyCredentials);
        });
    }

    @Test
    void testLogin_NonexistentEmail_ThrowsEntityNotFoundException() {
        String email = "test@gmail.com";
        String password = "test_password";
        CredentialsServiceDto credentials = new CredentialsServiceDto(email, password, null);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.login(credentials);
        });

        assertTrue(exception.getMessage().contains("User not found; email=" + email));
    }

    @Test
    void testLogin_NonexistentId_ThrowsEntityNotFoundException() {
        String fakeToken = "fake.jwt.token";
        UUID fakeId = UUID.randomUUID();
        CredentialsServiceDto credentials = new CredentialsServiceDto(null, null, fakeToken);

        Mockito.when(jwtService.extractUserId(fakeToken)).thenReturn(fakeId.toString());
        Mockito.when(userRepository.findById(fakeId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.login(credentials);
        });

        assertTrue(exception.getMessage().contains("User not found; id=" + fakeId));
    }

    @Test
    void testLogin_WithJwtToken_Success() throws CredentialException {
        String token = "valid.jwt.token";
        UUID userId = UUID.randomUUID();
        CredentialsServiceDto credentials = new CredentialsServiceDto(null, null, token);

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@gmail.com");

        Mockito.when(jwtService.extractUserId(token)).thenReturn(userId.toString());
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        LoginResultServiceDto result = authenticationService.login(credentials);

        assertTrue(result.success());
        assertEquals(userId, result.userId());
        assertEquals(token, result.jwtToken());
    }

    @Test
    void testLogin_WithEmail_CorrectPassword() throws CredentialException {
        String email = "test@gmail.com";
        String password = "correct_password";
        String newToken = "newly.generated.token";
        UUID userId = UUID.randomUUID();

        CredentialsServiceDto credentials = new CredentialsServiceDto(email, password, null);

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setEmail(email);
        mockUser.setPassword(password);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        // Учим сервис генерировать токен для этого юзера
        Mockito.when(jwtService.generate(userId)).thenReturn(newToken);

        LoginResultServiceDto result = authenticationService.login(credentials);

        assertTrue(result.success());
        assertEquals(newToken, result.jwtToken());
        assertEquals(userId, result.userId());
    }

    @Test
    void testLogin_WithEmail_WrongPassword() throws CredentialException {
        String email = "test@gmail.com";
        CredentialsServiceDto credentials = new CredentialsServiceDto(email, "wrong_password", null);

        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setPassword("correct_password");

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        LoginResultServiceDto result = authenticationService.login(credentials);

        assertFalse(result.success());
        assertNull(result.jwtToken());
    }

    @Test
    void testRegister_Success() {
        UserToRegisterServiceDto registerDto = new UserToRegisterServiceDto(
                "user",
                "test@gmail.com",
                "pass"
        );
        UUID userId = UUID.randomUUID();
        String generatedToken = "registered.jwt.token";

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("user");
        mockUser.setEmail("test@gmail.com");

        Mockito.when(userRepository.findByEmail(registerDto.email())).thenReturn(Optional.empty());
        Mockito.when(userMapper.userToRegisterServiceDtoToEntity(registerDto)).thenReturn(mockUser);
        Mockito.when(jwtService.generate(userId)).thenReturn(generatedToken);

        LoginResultServiceDto result = authenticationService.register(registerDto);

        assertTrue(result.success());
        assertEquals(generatedToken, result.jwtToken());
        assertEquals(userId, result.userId());
        Mockito.verify(userRepository).save(mockUser);
    }
}*/