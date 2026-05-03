package team.anonyms.converter.dto.controller.user;

import java.util.UUID;

public record UserControllerDto(
        UUID id,
        String username,
        String email,
        Boolean isVerified
) {
}
