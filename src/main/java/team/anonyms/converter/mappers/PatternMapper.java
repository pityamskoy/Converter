package team.anonyms.converter.mappers;

import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToCreateControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToUpdateControllerDto;
import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToUpdateServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToUpdateServiceDto;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.entities.User;

import java.util.List;

@Component
public class PatternMapper {
    private final ModificationMapper modificationMapper;

    public PatternMapper(ModificationMapper modificationMapper) {
        this.modificationMapper = modificationMapper;
    }

    public PatternToCreateServiceDto patternToCreateControllerDtoToService(
            PatternToCreateControllerDto patternToCreateControllerDto
    ) {
        List<ModificationToCreateServiceDto> modifications = patternToCreateControllerDto.modifications()
                .stream()
                .map(modificationMapper::modificationToCreateControllerDtoToService)
                .toList();

        return new PatternToCreateServiceDto(
                patternToCreateControllerDto.name(),
                modifications
        );
    }

    public PatternToUpdateServiceDto patternToUpdateControllerDtoToService(
            PatternToUpdateControllerDto patternToUpdateControllerDto
    ) {
        List<ModificationToUpdateServiceDto> modifications = patternToUpdateControllerDto.modifications()
                .stream()
                .map(modificationMapper::modificationToUpdateControllerDtoToService)
                .toList();

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

    public Pattern patternToCreateServiceDtoToEntity(PatternToCreateServiceDto patternToCreateServiceDto, User user) {
        return Pattern.builder()
                .name(patternToCreateServiceDto.name())
                .user(user)
                .build();
    }

    public PatternServiceDto patternToServiceDto(Pattern pattern) {
        return new PatternServiceDto(
                pattern.getId(),
                pattern.getName()
        );
    }
}
