package team.anonyms.converter.dto.service.user;

import java.util.UUID;

public record UserToUpdateServiceDto(
        UUID id,
        String username,
        String email,
        String password
) {
}
