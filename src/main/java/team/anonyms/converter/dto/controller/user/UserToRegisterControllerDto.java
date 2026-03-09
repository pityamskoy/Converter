package team.anonyms.converter.dto.controller.user;

public record UserToRegisterControllerDto(
        String username,
        String email,
        String password
) {
}
