package team.anonyms.converter.mappers;
import org.junit.jupiter.api.Test;
import team.anonyms.converter.dto.controller.authentication.CredentialsControllerDto;
import team.anonyms.converter.dto.controller.authentication.LoginResultControllerDto;
import team.anonyms.converter.dto.service.authentication.CredentialsServiceDto;
import team.anonyms.converter.dto.service.authentication.LoginResultServiceDto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationMapperTest {
    private final AuthenticationMapper mapper = new AuthenticationMapper();

    // тест на то, что все прогнанные через маппер поля точно перенеслись
    @Test
    void testCredentialsControllerDtoToService() {
        CredentialsControllerDto controllerDto = new CredentialsControllerDto(
                "test@gmail.com",
                "krutoy_parol123"
        );

        CredentialsServiceDto serviceDto = mapper.credentialsControllerDtoToService(controllerDto);

        assertEquals("test@gmail.com", serviceDto.email());
        assertEquals("krutoy_parol123", serviceDto.password());
    }

    // тест, что из сервисной дто-шки все нормально переносится в контроллер
    @Test
    void testLoginResultServiceDtoToController() {
        UUID fakeUserId = UUID.randomUUID();
        String fakeUsername = "fakeUsername";
        String fakeEmail = "fakeEmail";
        LoginResultServiceDto serviceDto = new LoginResultServiceDto(true, fakeUsername, fakeEmail, fakeUserId);

        LoginResultControllerDto controllerDto = mapper.loginResultServiceDtoToController(serviceDto);

        assertEquals(true, controllerDto.success());
        assertEquals(fakeUserId, controllerDto.userId());
    }
}