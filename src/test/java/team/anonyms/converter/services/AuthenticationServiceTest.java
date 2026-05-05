package team.anonyms.converter.services;

import jakarta.persistence.EntityNotFoundException;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import team.anonyms.converter.dto.service.credentials.CredentialsServiceDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.repositories.VerificationCodeRepository;
import team.anonyms.converter.services.frontend.AuthenticationService;
import team.anonyms.converter.services.frontend.JwtService;

import javax.security.auth.login.CredentialException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void testLogin_MissingCredentials_ThrowsCredentialException() {
        CredentialsServiceDto emptyCredentials = new CredentialsServiceDto(null, null);

        assertThrows(CredentialException.class, () -> {
            // Теперь передаем вторым аргументом null (токен)
            authenticationService.login(emptyCredentials, null);
        });
    }

    @Test
    void testLogin_NonexistentEmail_ThrowsEntityNotFoundException() {
        String email = "test@gmail.com";
        String password = "test_password";
        CredentialsServiceDto credentials = new CredentialsServiceDto(email, password);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.login(credentials, null);
        });

        assertTrue(exception.getMessage().contains("User not found; email=" + email));
    }

    @Test
    void testLogin_NonexistentId_ThrowsEntityNotFoundException() {
        String fakeToken = "fake.jwt.token";
        UUID fakeId = UUID.randomUUID();
        CredentialsServiceDto credentials = new CredentialsServiceDto(null, null);

        Mockito.when(jwtService.isValid(fakeToken)).thenReturn(true);
        Mockito.when(jwtService.extractUserId(fakeToken)).thenReturn(fakeId.toString());
        Mockito.when(userRepository.findById(fakeId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.login(credentials, fakeToken);
        });

        assertTrue(exception.getMessage().contains("User not found; id=" + fakeId));
    }

    @Test
    void testLogin_WithJwtToken_Success() throws CredentialException {
        String token = "valid.jwt.token";
        UUID userId = UUID.randomUUID();
        CredentialsServiceDto credentials = new CredentialsServiceDto(null, null);

        User mockUser = Mockito.mock(User.class);
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@gmail.com");

        Mockito.when(jwtService.isValid(token)).thenReturn(true);
        Mockito.when(jwtService.extractUserId(token)).thenReturn(userId.toString());
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        Pair<LoginResultServiceDto, String> result = authenticationService.login(credentials, token);

        assertTrue(result.a.success());
        assertEquals(userId, result.a.userId());
        assertEquals(token, result.b);
    }

    @Test
    void testLogin_WithEmail_CorrectPassword() throws CredentialException {
        String email = "test@gmail.com";
        String rawPassword = "correct_password";
        String encodedPassword = "encoded_password";
        String newToken = "newly.generated.token";
        UUID userId = UUID.randomUUID();

        CredentialsServiceDto credentials = new CredentialsServiceDto(email, rawPassword);

        User mockUser = Mockito.mock(User.class);
        Mockito.when(mockUser.getId()).thenReturn(userId);
        Mockito.when(mockUser.getUsername()).thenReturn("testuser");
        Mockito.when(mockUser.getEmail()).thenReturn(email);

        Mockito.when(mockUser.getPassword()).thenReturn(encodedPassword);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        Mockito.when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        Mockito.when(jwtService.generate(userId)).thenReturn(newToken);

        Pair<LoginResultServiceDto, String> result = authenticationService.login(credentials, null);

        assertTrue(result.a.success());
        assertEquals(userId, result.a.userId());
        assertEquals(newToken, result.b);
    }

    @Test
    void testLogin_WithEmail_WrongPassword() throws CredentialException {
        String email = "test@gmail.com";
        String rawPassword = "wrong_password";
        String encodedPassword = "encoded_password";

        CredentialsServiceDto credentials = new CredentialsServiceDto(email, rawPassword);

        User mockUser = Mockito.mock(User.class);

        Mockito.when(mockUser.getPassword()).thenReturn(encodedPassword);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        Mockito.when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        Pair<LoginResultServiceDto, String> result = authenticationService.login(credentials, null);

        assertFalse(result.a.success());
        assertNull(result.b);
    }
}