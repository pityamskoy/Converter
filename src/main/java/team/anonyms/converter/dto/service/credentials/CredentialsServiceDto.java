package team.anonyms.converter.dto.service.credentials;

import org.jspecify.annotations.Nullable;

public record CredentialsServiceDto(
        String email,
        String password,
        @Nullable String jwtToken
) {
}
