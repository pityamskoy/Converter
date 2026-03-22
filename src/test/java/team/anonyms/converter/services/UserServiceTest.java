package team.anonyms.converter.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateServiceDto;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.PatternMapper;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PatternMapper patternMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testUpdateUser_Success() {
        UUID userId = UUID.randomUUID();
        // дтошка со старыми данными
        UserToUpdateServiceDto updateDto = new UserToUpdateServiceDto(
                userId,
                "user",
                "new@gmail.com",
                "newpass",
                List.of()
        );

        // дтошка с новыми
        User mockUser = new User();
        UserServiceDto responseDto = new UserServiceDto(
                userId,
                "new_user",
                "new@gmail.com",
                List.of()
        );

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Mockito.when(userMapper.userToServiceDto(mockUser)).thenReturn(responseDto);

        UserServiceDto result = userService.updateUser(updateDto);

        // проверка, что пользователь не занулился, сохранился и его имя обновилось
        assertNotNull(result);
        assertEquals("new_user", result.username());
        Mockito.verify(userRepository).save(mockUser);
    }

    @Test
    void testUpdateUser_ThrowsEntityNotFound() {
        UUID userId = UUID.randomUUID();
        // надо обновить данные на эти, но юзера нет
        UserToUpdateServiceDto updateDto = new UserToUpdateServiceDto(userId,
                "name",
                "mail",
                "МЕГАНАИКРУТЕЙШИЙСИГМАПАССВОРД",
                List.of()
        );

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUser(updateDto);
        });
    }

    @Test
    void testDeleteUser_Success() {
        UUID userId = UUID.randomUUID();
        User mockUser = new User();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        userService.deleteUser(userId);

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