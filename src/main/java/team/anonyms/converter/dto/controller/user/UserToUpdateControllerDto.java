package team.anonyms.converter.dto.controller.user;

import java.util.UUID;

public record UserToUpdateControllerDto(
        UUID id,
        String username,
        String email,
        String password
) {
}
