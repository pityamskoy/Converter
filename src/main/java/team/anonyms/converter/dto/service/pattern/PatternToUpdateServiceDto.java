package team.anonyms.converter.dto.service.pattern;

import team.anonyms.converter.dto.service.modification.ModificationToUpdateServiceDto;

import java.util.List;
import java.util.UUID;

public record PatternToUpdateServiceDto(
        UUID id,
        String name,
        List<ModificationToUpdateServiceDto> modifications
) {
}
