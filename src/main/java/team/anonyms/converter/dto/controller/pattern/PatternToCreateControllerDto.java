package team.anonyms.converter.dto.controller.pattern;

import team.anonyms.converter.dto.controller.modification.ModificationToCreateControllerDto;

import java.util.List;
import java.util.UUID;

public record PatternToCreateControllerDto(
        UUID userId,
        String name,
        List<ModificationToCreateControllerDto> modifications
) {
}
