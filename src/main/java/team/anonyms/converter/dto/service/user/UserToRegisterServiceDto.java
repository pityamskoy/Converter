package team.anonyms.converter.dto.service.user;

public record UserToRegisterServiceDto(
        String username,
        String email,
        String password
) {
}
