package team.anonyms.converter.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.controller.modification.ModificationControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToCreateControllerDto;
import team.anonyms.converter.dto.service.modification.ModificationServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.entities.Modification;
import team.anonyms.converter.entities.Pattern;

import java.util.List;
import java.util.UUID;

@Component
public final class PatternMapper {
    @Autowired
    private ModificationMapper modificationMapper;

    public PatternServiceDto patternControllerDtoToServiceDto(PatternControllerDto patternControllerDto) {
        List<ModificationServiceDto> modifications = patternControllerDto.modifications().stream().
                map(modificationMapper::modificationControllerDtoToServiceDto).toList();

        return new PatternServiceDto(
                patternControllerDto.id(),
                patternControllerDto.conversionType(),
                patternControllerDto.instruction(),
                modifications
        );
    }

    public PatternToCreateServiceDto patternToCreateControllerDtoToService(
            PatternToCreateControllerDto patternToCreateControllerDto
    ) {
        List<ModificationServiceDto> modifications = patternToCreateControllerDto.modifications().stream().
                map(modificationMapper::modificationControllerDtoToServiceDto).toList();

        return new PatternToCreateServiceDto(
                patternToCreateControllerDto.userId(),
                patternToCreateControllerDto.conversionType(),
                patternToCreateControllerDto.instruction(),
                modifications
        );
    }

    public PatternControllerDto patternServiceDtoToControllerDto(PatternServiceDto patternServiceDto) {
        List<ModificationControllerDto> modifications = patternServiceDto.modifications().stream().
                map(modificationMapper::modificationServiceDtoToControllerDto).toList();

        return new PatternControllerDto(
                patternServiceDto.id(),
                patternServiceDto.conversionType(),
                patternServiceDto.instruction(),
                modifications
        );
    }

    public Pattern patternServiceDtoToEntity(PatternServiceDto patternServiceDto) {
        List<Modification> modifications = patternServiceDto.modifications().stream().
                map(modificationMapper::modificationServiceDtoToEntity).toList();

        return new Pattern(
                patternServiceDto.id(),
                patternServiceDto.conversionType(),
                patternServiceDto.instruction(),
                modifications
        );
    }

    public Pattern patternToCreateServiceDtoToEntity(PatternToCreateServiceDto patternToCreateServiceDto) {
        List<Modification> modifications = patternToCreateServiceDto.modifications().stream().
                map(modificationMapper::modificationServiceDtoToEntity).toList();

        return new Pattern(
                UUID.randomUUID(),
                patternToCreateServiceDto.conversionType(),
                patternToCreateServiceDto.instruction(),
                modifications
        );
    }

    public PatternServiceDto patternToServiceDto(Pattern pattern) {
        List<ModificationServiceDto> modifications = pattern.getModifications().stream().
                map(modificationMapper::modificationToServiceDto).toList();

        return new PatternServiceDto(
                pattern.getId(),
                pattern.getConversionType(),
                pattern.getInstruction(),
                modifications
        );
    }
}
