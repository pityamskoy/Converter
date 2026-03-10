package team.anonyms.converter.dto.controller.modification;

import java.util.UUID;

public record ModificationToCreateControllerDto(
        UUID patternId,
        String oldName,
        String newName,
        String newType,
        String newValue
) {
}
