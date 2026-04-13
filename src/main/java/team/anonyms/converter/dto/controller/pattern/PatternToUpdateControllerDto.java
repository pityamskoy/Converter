package team.anonyms.converter.dto.controller.pattern;

import team.anonyms.converter.dto.controller.modification.ModificationToUpdateControllerDto;

import java.util.List;
import java.util.UUID;

public record PatternToUpdateControllerDto(
        UUID id,
        String name,
        List<ModificationToUpdateControllerDto> modifications
) {
}
