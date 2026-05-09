package team.anonyms.converter.dto.controller.user;

import java.util.UUID;

public record UserToUpdateEmailControllerDto(
        UUID id,
        String email
) {
}
