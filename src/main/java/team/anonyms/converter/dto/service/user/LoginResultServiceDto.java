package team.anonyms.converter.dto.service.user;

import java.util.UUID;

public record LoginResultServiceDto(
        Boolean success,
        UUID userId
) {
}
