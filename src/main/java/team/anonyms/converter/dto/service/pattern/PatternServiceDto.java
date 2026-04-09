package team.anonyms.converter.dto.service.pattern;

import team.anonyms.converter.dto.service.modification.ModificationServiceDto;

import java.util.List;
import java.util.UUID;

public record PatternServiceDto(
        UUID id,
        String name,
        List<ModificationServiceDto> modifications
) {
}
