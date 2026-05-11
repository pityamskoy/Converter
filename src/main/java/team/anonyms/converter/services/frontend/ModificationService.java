package team.anonyms.converter.services.frontend;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import team.anonyms.converter.dto.service.modification.ModificationServiceDto;
import team.anonyms.converter.mappers.ModificationMapper;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ModificationService {
    private final ModificationRepository modificationRepository;
    private final PatternRepository patternRepository;
    private final ModificationMapper modificationMapper;

    public ModificationService(
            ModificationRepository modificationRepository,
            PatternRepository patternRepository,
            ModificationMapper modificationMapper
    ) {
        this.modificationRepository = modificationRepository;
        this.patternRepository = patternRepository;
        this.modificationMapper = modificationMapper;
    }

    public List<ModificationServiceDto> getAllModificationsByPatternId(UUID patternId) {
        if (!patternRepository.existsById(patternId)) {
            throw new EntityNotFoundException("Pattern not found; id=" + patternId);
        }

        return modificationRepository.findAllByPatternId(patternId).stream()
                .map(modificationMapper::modificationToServiceDto)
                .toList();
    }

    public Long getNumberOfAllModificationsByPatternId(UUID patternId) {
        if (!patternRepository.existsById(patternId)) {
            throw new EntityNotFoundException("Pattern not found; id=" + patternId);
        }

        return modificationRepository.countAllByPatternId(patternId);
    }
}
