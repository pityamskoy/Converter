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
import team.anonyms.converter.dto.service.pattern.PatternToUpdateServiceDto;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.ModificationMapper;
import team.anonyms.converter.mappers.PatternMapper;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.services.frontend.PatternService;

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
        Pattern mockPattern = Mockito.mock(Pattern.class);
        PatternServiceDto responseDto = new PatternServiceDto(UUID.randomUUID(), "name");

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(patternRepository.findAllByUserId(userId)).thenReturn(List.of(mockPattern));
        Mockito.when(patternMapper.patternToServiceDto(mockPattern)).thenReturn(responseDto);

        List<PatternServiceDto> result = patternService.getAllPatternsByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllPatternsByUserId_ThrowsEntityNotFound() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> patternService.getAllPatternsByUserId(userId)
        );

        assertEquals("User not found; id=" + userId, exception.getMessage());
    }

    @Test
    void testGetNumberOfAllPatternsByUserId_Success() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(patternRepository.countAllByUserId(userId)).thenReturn(2L);

        Long result = patternService.getNumberOfAllPatternsByUserId(userId);

        assertEquals(2L, result);
    }

    @Test
    void testCreatePattern_Success() {
        UUID userId = UUID.randomUUID();
        PatternToCreateServiceDto createDto = new PatternToCreateServiceDto(
                "name",
                List.of()
        );

        User mockUser = Mockito.mock(User.class);
        Pattern mockPattern = Mockito.mock(Pattern.class);

        PatternServiceDto responseDto = new PatternServiceDto(
                UUID.randomUUID(),
                "name"
        );

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        Mockito.when(patternMapper.patternToCreateServiceDtoToEntity(createDto, mockUser)).thenReturn(mockPattern);
        Mockito.when(patternMapper.patternToServiceDto(mockPattern)).thenReturn(responseDto);

        PatternServiceDto result = patternService.createPattern(createDto, userId);

        assertNotNull(result);

        Mockito.verify(patternRepository).save(mockPattern);
        Mockito.verify(modificationRepository).saveAll(Mockito.anyList());
    }

    @Test
    void testCreatePattern_ThrowsEntityNotFound() {
        UUID userId = UUID.randomUUID();
        PatternToCreateServiceDto createDto = new PatternToCreateServiceDto(
                "name",
                List.of()
        );
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> patternService.createPattern(createDto, userId)
        );

        assertEquals("User not found; id=" + userId, exception.getMessage());
    }

    /*
    @Test
    void testUpdatePattern_Success() {
        UUID patternId = UUID.randomUUID();
        PatternToUpdateServiceDto updateDto = new PatternToUpdateServiceDto(
                patternId,
                "newName",
                List.of()
        );

        PatternServiceDto dtoUpdated = new PatternServiceDto(
                patternId,
                "newName"
        );

        Pattern mockPattern = Mockito.mock(Pattern.class);
        Mockito.when(mockPattern.getId()).thenReturn(patternId);

        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.of(mockPattern));
        Mockito.when(patternMapper.patternToServiceDto(mockPattern)).thenReturn(dtoUpdated);

        PatternServiceDto result = patternService.updatePattern(updateDto);

        assertNotNull(result);
        Mockito.verify(mockPattern).setName("newName");

        Mockito.verify(modificationRepository).deleteAllByPatternId(patternId);
        Mockito.verify(patternRepository).save(mockPattern);
        Mockito.verify(modificationRepository).saveAll(Mockito.anyList());
    }

    @Test
    void testUpdatePattern_ThrowsEntityNotFound() {
        UUID patternId = UUID.randomUUID();
        PatternToUpdateServiceDto updateDto = new PatternToUpdateServiceDto(
                patternId,
                "name",
                List.of()
        );
        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> patternService.updatePattern(updateDto)
        );

        assertEquals("Pattern not found; id=" + patternId, exception.getMessage());
    }


    @Test
    void testDeletePattern_Success() {
        UUID patternId = UUID.randomUUID();
        Pattern mockPattern = Mockito.mock(Pattern.class);

        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.of(mockPattern));

        patternService.deletePattern(patternId);

        Mockito.verify(modificationRepository).deleteAllByPatternId(patternId);
        Mockito.verify(patternRepository).delete(mockPattern);
    }


    @Test
    void testDeletePattern_ThrowsEntityNotFound() {
        UUID patternId = UUID.randomUUID();

        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> patternService.deletePattern(patternId)
        );

        assertEquals("Pattern not found; patternId=" + patternId, exception.getMessage());
    }*/
}