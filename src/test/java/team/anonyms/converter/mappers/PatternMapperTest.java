package team.anonyms.converter.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToCreateControllerDto;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.entities.Pattern;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PatternMapperTest {

    // надо замокать, оно есть в PatternMapper. Иначе будут null-указатели
    @Mock
    private ModificationMapper modificationMapper;

    // mockito запихнет маппер сюда сам
    @InjectMocks
    private PatternMapper patternMapper;

    // а дальше также прогоняем поля во всех функциях, чтобы дтошки сервисов
    // возвращали дтошки контроллеров
    @Test
    void testPatternControllerDtoToServiceDto() {
        UUID id = UUID.randomUUID();
        PatternControllerDto controllerDto = new PatternControllerDto(
                id,
                "Pattern1",
                List.of()
        );

        PatternServiceDto serviceDto = patternMapper.patternControllerDtoToServiceDto(controllerDto);

        assertEquals(id, serviceDto.id());
        assertEquals("Pattern1", serviceDto.name());
    }

    @Test
    void testPatternToCreateControllerDtoToService() {
        UUID userId = UUID.randomUUID();
        PatternToCreateControllerDto controllerDto = new PatternToCreateControllerDto(
                userId,
                "Pattern1",
                List.of()
        );

        PatternToCreateServiceDto serviceDto = patternMapper.patternToCreateControllerDtoToService(controllerDto);

        assertEquals(userId, serviceDto.userId());
        assertEquals("Pattern1", serviceDto.name());
    }

    @Test
    void testPatternServiceDtoToControllerDto() {
        UUID id = UUID.randomUUID();
        PatternServiceDto serviceDto = new PatternServiceDto(
                id,
                "Pattern1",
                List.of()
        );

        PatternControllerDto controllerDto = patternMapper.patternServiceDtoToControllerDto(serviceDto);

        assertEquals(id, controllerDto.id());
        assertEquals("Pattern1", controllerDto.name());
    }

    @Test
    void testPatternServiceDtoToEntity() {
        UUID id = UUID.randomUUID();
        PatternServiceDto serviceDto = new PatternServiceDto(
                id,
                "Pattern1",
                List.of()
        );

        Pattern entity = patternMapper.patternServiceDtoToEntity(serviceDto);

        assertEquals(id, entity.getId());
        assertEquals("Pattern1", entity.getName());
    }

    @Test
    void testPatternToCreateServiceDtoToEntity() {
        UUID userId = UUID.randomUUID();
        PatternToCreateServiceDto serviceDto = new PatternToCreateServiceDto(
                userId,
                "Pattern1",
                List.of()
        );

        Pattern entity = patternMapper.patternToCreateServiceDtoToEntity(serviceDto);

        assertNotNull(entity.getId());
        assertEquals("Pattern1", entity.getName());
    }

    @Test
    void testPatternToServiceDto() {
        UUID id = UUID.randomUUID();
        Pattern entity = new Pattern(
                id,
                "Pattern1",
                List.of());

        PatternServiceDto serviceDto = patternMapper.patternToServiceDto(entity);

        assertEquals(id, serviceDto.id());
        assertEquals("Pattern1", serviceDto.name());
    }
}