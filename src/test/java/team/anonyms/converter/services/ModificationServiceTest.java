package team.anonyms.converter.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import team.anonyms.converter.dto.service.modification.ModificationServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;
import team.anonyms.converter.entities.Modification;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.mappers.ModificationMapper;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ModificationServiceTest {

    @Mock
    private ModificationRepository modificationRepository;
    @Mock
    private PatternRepository patternRepository;
    @Mock
    private ModificationMapper modificationMapper;

    @InjectMocks
    private ModificationService modificationService;

    @Test
    void testGetAllModificationsByPatternId_Success() {
        UUID patternId = UUID.randomUUID();
        Pattern mockPattern = new Pattern();
        // пустой список
        mockPattern.setModifications(new ArrayList<>());

        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.of(mockPattern));

        List<ModificationServiceDto> result = modificationService.getAllModificationsByPatternId(patternId);

        // проверяем, что из паттерна был корректно извлечен список и его размер равен 0
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetAllModificationsByPatternId_ThrowsEntityNotFound() {
        UUID patternId = UUID.randomUUID();
        // закидываем несуществующий паттерн в функцию извлечения модификаций
        // (якобы бдшка не нашла)
        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            modificationService.getAllModificationsByPatternId(patternId);
        });
        // должно быть правильное сообщение об ошибке
        assertTrue(exception.getMessage().contains("Pattern not found; id=" + patternId));
    }

    @Test
    void testCreateModification_Success() {
        UUID patternId = UUID.randomUUID();
        ModificationToCreateServiceDto createDto = new ModificationToCreateServiceDto(
                patternId,
                "old",
                "new",
                "type",
                "val"
        );

        Pattern mockPattern = new Pattern();
        mockPattern.setModifications(new ArrayList<>());

        Modification mockModification = new Modification();
        ModificationServiceDto responseDto = new ModificationServiceDto(
                UUID.randomUUID(),
                "old",
                "new",
                "type",
                "val"
        );

        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.of(mockPattern));
        Mockito.when(modificationMapper.modificationToCreateServiceDtoToEntity(createDto)).thenReturn(mockModification);
        Mockito.when(modificationMapper.modificationToServiceDto(mockModification)).thenReturn(responseDto);

        // из дтошек создается модификация
        ModificationServiceDto result = modificationService.createModification(createDto);

        // проверяется, что она корректно создалась и совпадает имя
        assertNotNull(result);
        assertEquals("old", result.oldName());
        // репы должны вызваться для сохранения
        Mockito.verify(modificationRepository).save(mockModification);
        Mockito.verify(patternRepository).save(mockPattern);
    }

    @Test
    void testCreateModification_ThrowsEntityNotFound() {
        UUID patternId = UUID.randomUUID();

        ModificationToCreateServiceDto createDto = new ModificationToCreateServiceDto(
                patternId,
                "old",
                "new",
                "type",
                "value"
        );

        Mockito.when(patternRepository.findById(patternId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            modificationService.createModification(createDto);
        });

        assertTrue(exception.getMessage().contains("Pattern not found; id=" + patternId));
    }

    @Test
    void testUpdateModification_Success() {
        UUID modId = UUID.randomUUID();
        ModificationServiceDto updateDto = new ModificationServiceDto(
                modId,
                "old",
                "new",
                "type",
                "val"
        );

        Modification mockModification = new Modification();

        Mockito.when(modificationRepository.findById(modId)).thenReturn(Optional.of(mockModification));
        Mockito.when(modificationMapper.modificationToServiceDto(mockModification)).thenReturn(updateDto);

        ModificationServiceDto result = modificationService.updateModification(updateDto);

        // проверка, что модификация обновилась и сохранилась
        assertNotNull(result);
        Mockito.verify(modificationRepository).save(mockModification);
    }

    @Test
    void testUpdateModification_ThrowsEntityNotFound() {
        UUID modificationId = UUID.randomUUID();
        ModificationServiceDto updateDto = new ModificationServiceDto(
                modificationId,
                "old",
                "new",
                "type",
                "value"
        );

        Mockito.when(modificationRepository.findById(modificationId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            modificationService.updateModification(updateDto);
        });

        assertTrue(exception.getMessage().contains("Modification not found; id=" + modificationId));
    }

    @Test
    void testDeleteModification_Success() {
        UUID modId = UUID.randomUUID();
        Modification mockModification = new Modification();

        Mockito.when(modificationRepository.findById(modId)).thenReturn(Optional.of(mockModification));

        modificationService.deleteModification(modId);

        Mockito.verify(modificationRepository).delete(mockModification);
    }

    @Test
    void testDeleteModification_ThrowsEntityNotFound() {
        UUID modificationId = UUID.randomUUID();

        Mockito.when(modificationRepository.findById(modificationId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            modificationService.deleteModification(modificationId);
        });

        assertTrue(exception.getMessage().contains("Modification not found; id=" + modificationId));
    }
}