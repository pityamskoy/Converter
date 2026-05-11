package team.anonyms.converter.dto.controller.authentication;

import java.util.UUID;

public record LoginResultControllerDto(
        Boolean success,
        String username,
        String email,
        UUID userId
) {
}
