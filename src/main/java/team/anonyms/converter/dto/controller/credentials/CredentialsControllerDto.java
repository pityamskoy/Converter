package team.anonyms.converter.dto.controller.credentials;

import org.jspecify.annotations.Nullable;

public record CredentialsControllerDto(
        String email,
        String password,
        @Nullable String jwtToken
) {
}
