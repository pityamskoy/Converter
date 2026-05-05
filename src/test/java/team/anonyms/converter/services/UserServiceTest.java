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
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateServiceDto;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.exceptions.EmailExistsException;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.repositories.VerificationCodeRepository;
import team.anonyms.converter.services.frontend.EmailService;
import team.anonyms.converter.services.frontend.JwtService;
import team.anonyms.converter.services.frontend.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private EmailService emailService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @Mock
    private PatternRepository patternRepository;
    @Mock
    private ModificationRepository modificationRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegister_Success() {
        UserToRegisterServiceDto dto = new UserToRegisterServiceDto("user", "test@gmail.com", "pass");
        UUID userId = UUID.randomUUID();

        User mockUser = Mockito.mock(User.class);
        Mockito.when(mockUser.getId()).thenReturn(userId);
        Mockito.when(mockUser.getUsername()).thenReturn("user");
        Mockito.when(mockUser.getEmail()).thenReturn("test@gmail.com");

        Mockito.when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        Mockito.when(userMapper.userToRegisterServiceDtoToEntity(dto)).thenReturn(mockUser);
        Mockito.when(passwordEncoder.encode(dto.password())).thenReturn("encoded_pass");
        Mockito.when(userRepository.save(mockUser)).thenReturn(mockUser);
        Mockito.when(jwtService.generate(userId)).thenReturn("jwt.token.here");

        Pair<LoginResultServiceDto, String> result = userService.register(dto);

        assertTrue(result.a.success());
        assertEquals("jwt.token.here", result.b);

        Mockito.verify(emailService).sendVerificationCode(mockUser);
        Mockito.verify(mockUser).setPassword("encoded_pass");
    }

    @Test
    void testRegister_ThrowsEmailExistsException() {
        UserToRegisterServiceDto dto = new UserToRegisterServiceDto("user", "exist@gmail.com", "pass");

        User mockUser = Mockito.mock(User.class);
        Mockito.when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(mockUser));

        assertThrows(EmailExistsException.class, () -> {
            userService.register(dto);
        });
    }

    @Test
    void testUpdateUser_Success() {
        UUID userId = UUID.randomUUID();
        UserToUpdateServiceDto updateDto = new UserToUpdateServiceDto(
                userId,
                "new_user",
                "new@gmail.com",
                "newpass"
        );

        User mockUser = Mockito.mock(User.class);
        UserServiceDto responseDto = new UserServiceDto(
                userId,
                "new_user",
                "new@gmail.com",
                true
        );

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Mockito.when(passwordEncoder.encode("newpass")).thenReturn("encoded_newpass");
        Mockito.when(userMapper.userToServiceDto(mockUser)).thenReturn(responseDto);

        UserServiceDto result = userService.updateUser(updateDto);

        assertNotNull(result);
        assertEquals("new_user", result.username());

        Mockito.verify(mockUser).setUsername("new_user");
        Mockito.verify(mockUser).setEmail("new@gmail.com");
        Mockito.verify(mockUser).setPassword("encoded_newpass");

        Mockito.verify(userRepository).save(mockUser);
    }

    @Test
    void testUpdateUser_ThrowsEntityNotFound() {
        UUID userId = UUID.randomUUID();
        UserToUpdateServiceDto updateDto = new UserToUpdateServiceDto(
                userId,
                "name",
                "mail",
                "МЕГАНАИКРУТЕЙШИЙСИГМАПАССВОРД"
        );

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUser(updateDto);
        });
    }

    @Test
    void testDeleteUser_Success() {
        UUID userId = UUID.randomUUID();
        UUID patternId = UUID.randomUUID();

        User mockUser = Mockito.mock(User.class);
        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Mockito.when(patternRepository.findAllByUserId(userId)).thenReturn(List.of(mockPattern));

        userService.deleteUser(userId);

        Mockito.verify(modificationRepository).deleteAllByPatternId(patternId);
        Mockito.verify(patternRepository).deleteAllByUserId(userId);
        Mockito.verify(verificationCodeRepository).deleteByUserId(userId);
        Mockito.verify(userRepository).delete(mockUser);
    }

    @Test
    void testDeleteUser_ThrowsEntityNotFound() {
        UUID userId = UUID.randomUUID();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });
    }
}