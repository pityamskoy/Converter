package team.anonyms.converter.dto.controller.user;

import java.util.UUID;

public record LoginResultControllerDto(
        Boolean success,
        UUID userId
) {
}
