package team.anonyms.converter.dto.service.modification;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record ModificationToUpdateServiceDto(
        @Nullable UUID id,
        String oldName,
        String newName,
        String newValue,
        String newType
) {
}
