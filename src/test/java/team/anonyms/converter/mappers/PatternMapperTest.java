package team.anonyms.converter.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToCreateControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToUpdateControllerDto;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToUpdateServiceDto;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.entities.User;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PatternMapperTest {

    @Mock
    private ModificationMapper modificationMapper;

    @InjectMocks
    private PatternMapper patternMapper;

    @Test
    void testPatternToCreateControllerDtoToService() {
        UUID userId = UUID.randomUUID();
        PatternToCreateControllerDto controllerDto = new PatternToCreateControllerDto(
                "Pattern1",
                List.of()
        );

        PatternToCreateServiceDto serviceDto = patternMapper.patternToCreateControllerDtoToService(controllerDto);

        assertEquals("Pattern1", serviceDto.name());
        assertTrue(serviceDto.modifications().isEmpty());
    }

    @Test
    void testPatternToUpdateControllerDtoToService() {
        UUID id = UUID.randomUUID();
        PatternToUpdateControllerDto controllerDto = new PatternToUpdateControllerDto(
                id,
                "Pattern1",
                List.of()
        );

        PatternToUpdateServiceDto serviceDto = patternMapper.patternToUpdateControllerDtoToService(controllerDto);

        assertEquals(id, serviceDto.id());
        assertEquals("Pattern1", serviceDto.name());
        assertTrue(serviceDto.modifications().isEmpty());
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
    void testPatternToCreateServiceDtoToEntity() {
        UUID userId = UUID.randomUUID();
        PatternToCreateServiceDto serviceDto = new PatternToCreateServiceDto(
                "Pattern1",
                List.of()
        );

        User mockUser = Mockito.mock(User.class);

        Pattern entity = patternMapper.patternToCreateServiceDtoToEntity(serviceDto, mockUser);

        assertNull(entity.getId());
        assertEquals("Pattern1", entity.getName());
        assertEquals(mockUser, entity.getUser());
    }

    @Test
    void testPatternToServiceDto() {
        UUID id = UUID.randomUUID();

        Pattern mockEntity = Mockito.mock(Pattern.class);
        Mockito.when(mockEntity.getId()).thenReturn(id);
        Mockito.when(mockEntity.getName()).thenReturn("Pattern1");

        PatternServiceDto serviceDto = patternMapper.patternToServiceDto(mockEntity);

        assertEquals(id, serviceDto.id());
        assertEquals("Pattern1", serviceDto.name());
    }
}