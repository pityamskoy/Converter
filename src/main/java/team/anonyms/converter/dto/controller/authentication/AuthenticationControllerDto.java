package team.anonyms.converter.dto.controller.authentication;

public record AuthenticationControllerDto(
        LoginResultControllerDto result,
        String jwtToken
) {
}
