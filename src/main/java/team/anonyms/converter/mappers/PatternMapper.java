package team.anonyms.converter.mappers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToCreateControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToUpdateControllerDto;
import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToUpdateServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToUpdateServiceDto;
import team.anonyms.converter.entities.Modification;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.repositories.PatternRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public final class PatternMapper {
    private final PatternRepository patternRepository;
    private final ModificationMapper modificationMapper;

    public PatternMapper(PatternRepository patternRepository, ModificationMapper modificationMapper) {
        this.patternRepository = patternRepository;
        this.modificationMapper = modificationMapper;
    }

    public PatternServiceDto patternControllerDtoToServiceDto(PatternControllerDto patternControllerDto) {
        return new PatternServiceDto(
                patternControllerDto.id(),
                patternControllerDto.name()
        );
    }

    public PatternToCreateServiceDto patternToCreateControllerDtoToService(
            PatternToCreateControllerDto patternToCreateControllerDto
    ) {
        List<ModificationToCreateServiceDto> modifications = patternToCreateControllerDto.modifications().stream().
                map(modificationMapper::modificationToCreateControllerDtoToService).toList();

        return new PatternToCreateServiceDto(
                patternToCreateControllerDto.userId(),
                patternToCreateControllerDto.name(),
                modifications
        );
    }

    public PatternToUpdateServiceDto patternToUpdateControllerDtoToService(
            PatternToUpdateControllerDto patternToUpdateControllerDto
    ) {
        List<ModificationToUpdateServiceDto> modifications = patternToUpdateControllerDto.modifications().stream().
                map(modificationMapper::modificationToUpdateControllerDtoToService).toList();

        return new PatternToUpdateServiceDto(
                patternToUpdateControllerDto.id(),
                patternToUpdateControllerDto.name(),
                modifications
        );
    }

    public PatternControllerDto patternServiceDtoToControllerDto(PatternServiceDto patternServiceDto) {
        return new PatternControllerDto(
                patternServiceDto.id(),
                patternServiceDto.name()
        );
    }

    public Pattern patternServiceDtoToEntity(PatternServiceDto patternServiceDto) {
        Optional<Pattern> patternOptional = patternRepository.findById(patternServiceDto.id());

        if (patternOptional.isEmpty()) {
            throw new EntityNotFoundException("Pattern not found; id=" + patternServiceDto.id());
        }

        List<Modification> modifications = patternOptional.get().getModifications();

        return new Pattern(
                patternServiceDto.id(),
                patternServiceDto.name(),
                modifications
        );
    }

    public Pattern patternToCreateServiceDtoToEntity(PatternToCreateServiceDto patternToCreateServiceDto) {
        List<Modification> modifications = patternToCreateServiceDto.modifications().stream().
                map(modificationMapper::modificationToCreateServiceDtoToEntity).toList();

        return new Pattern(
                UUID.randomUUID(),
                patternToCreateServiceDto.name(),
                modifications
        );
    }

    public PatternServiceDto patternToServiceDto(Pattern pattern) {
        return new PatternServiceDto(
                pattern.getId(),
                pattern.getName()
        );
    }
}
