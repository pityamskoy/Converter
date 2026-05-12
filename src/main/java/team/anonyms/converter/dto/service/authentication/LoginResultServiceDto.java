package team.anonyms.converter.dto.service.authentication;

import java.util.UUID;

public record LoginResultServiceDto(
        Boolean success,
        UUID userId,
        String username,
        String email,
        Boolean isVerified
) {
}
