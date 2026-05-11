package team.anonyms.converter.dto.service.authentication;

public record PasswordResetServiceDto(
        String email,
        String verificationCode,
        String newPassword
) {
}
