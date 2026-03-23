package team.anonyms.converter.dto.controller.credentials;

import java.util.UUID;

public record LoginResultControllerDto(
        Boolean success,
        String username,
        UUID userId
) {
}
