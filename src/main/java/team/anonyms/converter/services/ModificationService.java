package team.anonyms.converter.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.anonyms.converter.dto.service.modification.ModificationServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;
import team.anonyms.converter.entities.Modification;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.mappers.ModificationMapper;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public final class ModificationService {
    @Autowired
    private ModificationRepository modificationRepository;
    @Autowired
    private PatternRepository patternRepository;
    @Autowired
    private ModificationMapper modificationMapper;

    public List<ModificationServiceDto> getAllModificationsByPatternId(UUID patternId) {
        Optional<Pattern> patternOptional = patternRepository.findById(patternId);

        if (patternOptional.isEmpty()) {
            throw new EntityNotFoundException("Pattern not found; id=" + patternId);
        }

        List<Modification> modifications = patternOptional.get().getModifications();

        return modifications.stream().map(modificationMapper::modificationToServiceDto).toList();
    }

    public ModificationServiceDto createModification(ModificationToCreateServiceDto modificationToCreate) {
        Optional<Pattern> patternOptional = patternRepository.findById(modificationToCreate.patternId());

        if (patternOptional.isEmpty()) {
            throw new EntityNotFoundException("Pattern not found; id=" + modificationToCreate.patternId());
        }

        Modification modificationCreated = modificationMapper.
                modificationToCreateServiceDtoToEntity(modificationToCreate);
        modificationRepository.save(modificationCreated);

        Pattern pattern = patternOptional.get();
        List<Modification> modifications = pattern.getModifications();
        modifications.add(modificationCreated);

        pattern.setModifications(modifications);
        patternRepository.save(pattern);

        return modificationMapper.modificationToServiceDto(modificationCreated);
    }

    public ModificationServiceDto updateModification(ModificationServiceDto modificationToUpdate) {
        Optional<Modification> modificationOptional = modificationRepository.findById(modificationToUpdate.id());

        if (modificationOptional.isEmpty()) {
            throw new EntityNotFoundException("Modification not found; id=" + modificationToUpdate.id());
        }

        Modification modificationUpdated = modificationOptional.get();
        modificationUpdated.setOldName(modificationToUpdate.oldName());
        modificationUpdated.setNewName(modificationToUpdate.newName());
        modificationUpdated.setNewType(modificationToUpdate.newType());
        modificationUpdated.setNewValue(modificationToUpdate.newValue());

        modificationRepository.save(modificationUpdated);

        return modificationMapper.modificationToServiceDto(modificationUpdated);
    }

    public void deleteModification(UUID modificationId) {
        Optional<Modification> modificationOptional = modificationRepository.findById(modificationId);

        if (modificationOptional.isEmpty()) {
            throw new EntityNotFoundException("Modification not found; id=" + modificationId);
        }

        modificationRepository.delete(modificationOptional.get());
    }
}
