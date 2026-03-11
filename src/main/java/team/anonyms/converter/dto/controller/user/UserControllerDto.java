package team.anonyms.converter.dto.controller.user;

import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;

import java.util.List;
import java.util.UUID;

public record UserControllerDto(
        UUID id,
        String username,
        String email,
        List<PatternControllerDto> patterns
) {
}
