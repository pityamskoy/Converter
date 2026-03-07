package team.anonyms.converter.dto.service.user;

public record UserToCreateServiceDto(
        String username,
        String email,
        String password
) {
}
