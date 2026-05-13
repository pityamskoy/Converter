package team.anonyms.converter.mappers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import team.anonyms.converter.dto.controller.user.UserControllerDto;
import team.anonyms.converter.dto.controller.user.UserToRegisterControllerDto;
import team.anonyms.converter.dto.controller.user.UserToUpdateControllerDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateServiceDto;
import team.anonyms.converter.entities.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// Update needed
class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

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
        UserToUpdateControllerDto controllerDto = new UserToUpdateControllerDto(
                "newname",
                "mega_krutoi_parol"
        );

        UserToUpdateServiceDto serviceDto = userMapper.userToUpdateControllerDtoToService(controllerDto);

        assertEquals("newname", serviceDto.username());
        assertEquals("mega_krutoi_parol", serviceDto.password());
    }

    @Test
    void testUserServiceDtoToControllerDto() {
        UUID id = UUID.randomUUID();
        UserServiceDto serviceDto = new UserServiceDto(
                id,
                "username",
                "test@gmail.com",
                true
        );

        UserControllerDto controllerDto = userMapper.userServiceDtoToControllerDto(serviceDto);

        assertEquals(id, controllerDto.id());
        assertEquals("username", controllerDto.username());
        assertEquals("test@gmail.com", controllerDto.email());
        assertTrue(controllerDto.isVerified());
    }

    @Test
    void testUserToRegisterServiceDtoToEntity() {
        UserToRegisterServiceDto serviceDto = new UserToRegisterServiceDto(
                "username",
                "test@gmail.com",
                "mega_krutoi_parol"
        );

        User entity = userMapper.userToRegisterServiceDtoToEntity(serviceDto);

        assertNull(entity.getId());
        assertEquals("username", entity.getUsername());
        assertEquals("test@gmail.com", entity.getEmail());
        assertEquals("mega_krutoi_parol", entity.getPassword());
        assertFalse(entity.getIsVerified());
    }

    @Test
    void testUserToServiceDto() {
        UUID id = UUID.randomUUID();
        User mockEntity = Mockito.mock(User.class);

        Mockito.when(mockEntity.getId()).thenReturn(id);
        Mockito.when(mockEntity.getUsername()).thenReturn("username");
        Mockito.when(mockEntity.getEmail()).thenReturn("test@gmail.com");
        Mockito.when(mockEntity.getIsVerified()).thenReturn(true);

        UserServiceDto serviceDto = userMapper.userToServiceDto(mockEntity);

        assertEquals(id, serviceDto.id());
        assertEquals("username", serviceDto.username());
        assertEquals("test@gmail.com", serviceDto.email());
        assertTrue(serviceDto.isVerified());
    }
}