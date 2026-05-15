package team.anonyms.converter.dto.service.user;

import java.util.UUID;

public record UserServiceDto(
        UUID id,
        String username,
        String email,
        Boolean isVerified
) {
}
