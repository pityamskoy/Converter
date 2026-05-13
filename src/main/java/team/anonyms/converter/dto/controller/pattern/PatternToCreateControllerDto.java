package team.anonyms.converter.dto.controller.pattern;

import team.anonyms.converter.dto.controller.modification.ModificationToCreateControllerDto;

import java.util.List;

public record PatternToCreateControllerDto(
        String name,
        List<ModificationToCreateControllerDto> modifications
) {
}
