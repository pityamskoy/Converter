package team.anonyms.converter.dto.service.user;

import java.util.UUID;

public record UserToUpdateEmailServiceDto(
        UUID id,
        String email
) {
}
