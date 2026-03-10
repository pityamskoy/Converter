package team.anonyms.converter.dto.service.user;

import team.anonyms.converter.dto.service.pattern.PatternServiceDto;

import java.util.List;
import java.util.UUID;

public record UserToUpdateServiceDto(
        UUID id,
        String username,
        String email,
        String password,
        List<PatternServiceDto> patterns
) {
}
