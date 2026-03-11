package team.anonyms.converter.dto.service.user;

import team.anonyms.converter.dto.service.pattern.PatternServiceDto;

import java.util.List;
import java.util.UUID;

public record UserServiceDto(
        UUID id,
        String username,
        String email,
        List<PatternServiceDto> patterns
) {
}
