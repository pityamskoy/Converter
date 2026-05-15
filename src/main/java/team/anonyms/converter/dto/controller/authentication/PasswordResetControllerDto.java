package team.anonyms.converter.dto.controller.authentication;

public record PasswordResetControllerDto(
        String email,
        String verificationCode,
        String newPassword
) {
}
