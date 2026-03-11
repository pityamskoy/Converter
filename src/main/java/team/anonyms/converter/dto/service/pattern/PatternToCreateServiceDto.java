package team.anonyms.converter.dto.service.pattern;

import team.anonyms.converter.dto.service.modification.ModificationServiceDto;

import java.util.List;
import java.util.UUID;

public record PatternToCreateServiceDto(
        UUID userId,
        String name,
        String conversionType,
        String instruction,
        List<ModificationServiceDto> modifications
) {
}
