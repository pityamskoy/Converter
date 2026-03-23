package team.anonyms.converter.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team.anonyms.converter.dto.controller.user.UserControllerDto;
import team.anonyms.converter.dto.controller.user.UserToRegisterControllerDto;
import team.anonyms.converter.dto.controller.user.UserToUpdateControllerDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateServiceDto;
import team.anonyms.converter.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    // нужно замокать
    @Mock
    private PatternMapper patternMapper;

    // сюда через mockito подгрузится само
    @InjectMocks
    private UserMapper userMapper;

    // прогон полей и проверка
    @Test
    void testUserToRegisterControllerDtoToService() {
        UserToRegisterControllerDto controllerDto = new UserToRegisterControllerDto(
                "username",
                "test@gmail.com",
                "mega_krutoi_parol"
        );

        UserToRegisterServiceDto serviceDto = userMapper.userToRegisterControllerDtoToService(controllerDto);

        assertEquals("username", serviceDto.username());
        assertEquals("test@gmail.com", serviceDto.email());
        assertEquals("mega_krutoi_parol", serviceDto.password());
    }

    @Test
    void testUserToUpdateControllerDtoToService() {
        UUID id = UUID.randomUUID();
        UserToUpdateControllerDto controllerDto = new UserToUpdateControllerDto(
                id, "newname",
                "newtest@gmail.com",
                "mega_krutoi_parol",
                List.of()
        );

        UserToUpdateServiceDto serviceDto = userMapper.userToUpdateControllerDtoToService(controllerDto);

        assertEquals(id, serviceDto.id());
        assertEquals("newname", serviceDto.username());
        assertEquals("newtest@gmail.com", serviceDto.email());
        assertEquals("mega_krutoi_parol", serviceDto.password());
    }

    @Test
    void testUserServiceDtoToControllerDto() {
        UUID id = UUID.randomUUID();
        UserServiceDto serviceDto = new UserServiceDto(
                id, "username",
                "test@gmail.com",
                List.of()
        );

        UserControllerDto controllerDto = userMapper.userServiceDtoToControllerDto(serviceDto);

        assertEquals(id, controllerDto.id());
        assertEquals("username", controllerDto.username());
        assertEquals("test@gmail.com", controllerDto.email());
    }

    @Test
    void testUserToRegisterServiceDtoToEntity() {
        UserToRegisterServiceDto serviceDto = new UserToRegisterServiceDto(
                "username",
                "test@gmail.com",
                "mega_krutoi_parol"
        );

        User entity = userMapper.userToRegisterServiceDtoToEntity(serviceDto);

        assertNotNull(entity.getId());
        assertEquals("username", entity.getUsername());
        assertEquals("test@gmail.com", entity.getEmail());
        assertEquals("mega_krutoi_parol", entity.getPassword());
        assertNotNull(entity.getPatterns());
    }

    @Test
    void testUserToServiceDto() {
        UUID id = UUID.randomUUID();
        User entity = new User(
                id,
                "username",
                "test@gmail.com",
                "mega_krutoi_parol",
                new ArrayList<>()
        );

        UserServiceDto serviceDto = userMapper.userToServiceDto(entity);

        assertEquals(id, serviceDto.id());
        assertEquals("username", serviceDto.username());
        assertEquals("test@gmail.com", serviceDto.email());
    }
}