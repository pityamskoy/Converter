package team.anonyms.converter.dto.controller.pattern;

import team.anonyms.converter.dto.controller.modification.ModificationControllerDto;

import java.util.List;
import java.util.UUID;

public record PatternToCreateControllerDto(
        UUID userId,
        String name,
        String conversionType,
        List<ModificationControllerDto> modifications
) {
}
