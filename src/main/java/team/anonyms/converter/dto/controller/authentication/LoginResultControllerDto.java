package team.anonyms.converter.dto.controller.authentication;

import java.util.UUID;

public record LoginResultControllerDto(
        Boolean success,
        UUID userId,
        String username,
        String email,
        Boolean isVerified
) {
}
