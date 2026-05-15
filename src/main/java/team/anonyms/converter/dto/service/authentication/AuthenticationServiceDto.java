package team.anonyms.converter.dto.service.authentication;

public record AuthenticationServiceDto(
        LoginResultServiceDto result,
        String jwtToken
) {
}
