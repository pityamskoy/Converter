package team.anonyms.converter.dto.controller.user;

public record UserToCreateControllerDto(
        String username,
        String email,
        String password
) {
}
