package team.anonyms.converter.dto.service.credentials;

import java.util.UUID;

public record LoginResultServiceDto(
        Boolean success,
        String username,
        String email,
        UUID userId
) {
}
