package team.anonyms.converter.dto.service.modification;

import java.util.UUID;

public record ModificationServiceDto(
        UUID id,
        String oldName,
        String newName,
        String newValue,
        String newType
) {
}
