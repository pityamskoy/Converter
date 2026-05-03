package team.anonyms.converter.mappers;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToCreateControllerDto;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.repositories.PatternRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
/*
@ExtendWith(MockitoExtension.class)
class PatternMapperTest {

    // надо замокать, оно есть в PatternMapper. Иначе будут null-указатели
    @Mock
    private ModificationMapper modificationMapper;

    @Mock
    private PatternRepository patternRepository;

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
                "Pattern1"
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
                "Pattern1"
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
                "Pattern1"
        );

        Pattern entityToSave = new Pattern(id, "Pattern1", List.of());


        Mockito.when(patternRepository.findById(id)).thenReturn(Optional.of(entityToSave));
        Pattern entity = patternMapper.patternServiceDtoToEntity(serviceDto);

        assertEquals(id, entity.getId());
        assertEquals("Pattern1", entity.getName());


        assert(true);
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

    @Test
    void testPatternServiceDtoToEntity_ThrowsEntityNotFound() {
        UUID id = UUID.randomUUID();
        PatternServiceDto serviceDto = new PatternServiceDto(id, "Pattern1");

        Mockito.when(patternRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> patternMapper.patternServiceDtoToEntity(serviceDto)
        );

        assertEquals("Pattern not found; id=" + id, exception.getMessage());
    }
}*/