package team.anonyms.converter.dto.service.modification;

import java.util.UUID;

public record ModificationToCreateServiceDto(
        UUID patternId,
        String oldName,
        String newName,
        String newType,
        String newValue
) {
}
