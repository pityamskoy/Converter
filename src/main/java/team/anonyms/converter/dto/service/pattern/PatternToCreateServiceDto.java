package team.anonyms.converter.dto.service.pattern;

import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;

import java.util.List;
import java.util.UUID;

public record PatternToCreateServiceDto(
        String name,
        List<ModificationToCreateServiceDto> modifications
) {
}
