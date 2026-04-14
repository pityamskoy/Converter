package team.anonyms.converter.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.ModificationMapper;
import team.anonyms.converter.mappers.PatternMapper;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.services.frontend.PatternService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PatternServiceTest {

    @Mock
    private PatternRepository patternRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PatternMapper patternMapper;
    @Mock
    private ModificationRepository modificationRepository;
    @Mock
    private ModificationMapper modificationMapper;

    @InjectMocks
    private PatternService patternService;

    @Test
    void testGetAllPatternsByUserId_Success() {
        UUID userId = UUID.randomUUID();
        // пусть будет пустой конструктор
        User mockUser = new User();
        mockUser.setPatterns(new ArrayList<>());

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        List<PatternServiceDto> result = patternService.getAllPatternsByUserId(userId);

        // проверяем, что список паттернов пользователя есть и его длина 0
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetAllPatternsByUserId_ThrowsEntityNotFound() {
        UUID userId = UUID.randomUUID();
        // бд не нашла пользователя с данным id
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // проверка, что выкидывает правильное исключение
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patternService.getAllPatternsByUserId(userId);
        });
        assertEquals("User not found; id="+userId, exception.getMessage());
    }
    /*
    @Test
    void testCreatePattern_Success() {
        UUID userId = UUID.randomUUID();
        // дтошки для сервисов, конструктор юзера и пустого списка паттернов
        PatternToCreateServiceDto createDto = new PatternToCreateServiceDto(
                userId,
                "name",
                List.of()
        );

        User mockUser = new User();
        mockUser.setPatterns(new ArrayList<>());

        Pattern mockPattern = new Pattern();
        PatternServiceDto responseDto = new PatternServiceDto(
                UUID.randomUUID(),
                "name",
                List.of()
        );

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Mockito.when(patternMapper.patternToCreateServiceDtoToEntity(createDto)).thenReturn(mockPattern);
        Mockito.when(patternMapper.patternToServiceDto(mockPattern)).thenReturn(responseDto);

        // fix this
        PatternServiceDto result = patternService.createPattern(createDto);

        // паттерн вернулся и сохранился в репы
        assertNotNull(result);
        Mockito.verify(patternRepository).save(mockPattern);
        Mockito.verify(userRepository).save(mockUser);
    }*/

    @Test
    void testCreatePattern_ThrowsEntityNotFound() {
        UUID userId = UUID.randomUUID();
        PatternToCreateServiceDto createDto = new PatternToCreateServiceDto(
                userId,
                "name",
                List.of()
        );
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patternService.createPattern(createDto);
        });
        assertEquals("User not found; id="+userId, exception.getMessage());
    }

    @Test
    void testUpdatePattern_Success() {
        UUID patternId = UUID.randomUUID();
        // данные для обновления паттерна
        PatternServiceDto updateDto = new PatternServiceDto(
                patternId,
                "name",
                List.of()
        );

        Pattern mockPattern = new Pattern();

        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.of(mockPattern));
        Mockito.when(patternMapper.patternToServiceDto(mockPattern)).thenReturn(updateDto);

        PatternServiceDto result = patternService.updatePattern(updateDto);

        // паттерн обновился и сохранился
        assertNotNull(result);
        Mockito.verify(patternRepository).save(mockPattern);
    }

    @Test
    void testUpdatePattern_ThrowsEntityNotFound() {
        UUID patternId = UUID.randomUUID();
        PatternServiceDto updateDto = new PatternServiceDto(
                patternId,
                "name",
                List.of()
        );
        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patternService.updatePattern(updateDto);
        });
        assertEquals("Pattern not found; id="+patternId, exception.getMessage());
    }

    @Test
    void testDeletePattern_Success() {
        UUID patternId = UUID.randomUUID();
        Pattern mockPattern = new Pattern();

        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.of(mockPattern));

        patternService.deletePattern(patternId);

        Mockito.verify(patternRepository).delete(mockPattern);
    }

    @Test
    void testDeletePattern_ThrowsEntityNotFound() {
        UUID patternId = UUID.randomUUID();

        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patternService.deletePattern(patternId);
        });
        assertEquals("Pattern not found; id="+patternId, exception.getMessage());
    }
}