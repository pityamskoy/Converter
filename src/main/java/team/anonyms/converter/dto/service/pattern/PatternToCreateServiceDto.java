package team.anonyms.converter.dto.service.pattern;

import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;

import java.util.List;

public record PatternToCreateServiceDto(
        String name,
        List<ModificationToCreateServiceDto> modifications
) {
}
