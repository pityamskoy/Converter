package team.anonyms.converter.services.frontend;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import team.anonyms.converter.dto.service.modification.ModificationServiceDto;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.mappers.ModificationMapper;
import team.anonyms.converter.repositories.PatternRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public final class ModificationService {
    private final PatternRepository patternRepository;
    private final ModificationMapper modificationMapper;

    public ModificationService(
            PatternRepository patternRepository,
            ModificationMapper modificationMapper
    ) {
        this.patternRepository = patternRepository;
        this.modificationMapper = modificationMapper;
    }

    public List<ModificationServiceDto> getAllModificationsByPatternId(UUID id) {
        Optional<Pattern> patternOptional = patternRepository.findById(id);
        if (patternOptional.isEmpty()) {
            throw new EntityNotFoundException("Pattern not found; id=" + id);
        }

        return patternOptional.get().getModifications().
                stream().map(modificationMapper::modificationToServiceDto).toList();
    }

    public Integer getNumberOfAllModificationsByPatternId(UUID id) {
        Optional<Pattern> patternOptional = patternRepository.findById(id);
        if (patternOptional.isEmpty()) {
            throw new EntityNotFoundException("Pattern not found; id=" + id);
        }

        return patternOptional.get().getModifications().size();
    }
}
