package team.anonyms.converter.dto.controller.modification;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record ModificationToUpdateControllerDto(
        @Nullable UUID id,
        String oldName,
        String newName,
        String newType,
        String newValue
) {
}
