package team.anonyms.converter.dto.controller.pattern;

import team.anonyms.converter.dto.controller.modification.ModificationControllerDto;

import java.util.List;
import java.util.UUID;

public record PatternControllerDto(
        UUID id,
        String name,
        String conversionType,
        String instruction,
        List<ModificationControllerDto> modifications
) {
}
