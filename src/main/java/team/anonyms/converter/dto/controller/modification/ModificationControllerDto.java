package team.anonyms.converter.dto.controller.modification;

import java.util.UUID;

public record ModificationControllerDto(
        UUID id,
        String oldName,
        String newName,
        String newValue,
        String newType
) {
}
