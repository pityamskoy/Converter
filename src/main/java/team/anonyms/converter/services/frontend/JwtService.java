package team.anonyms.converter.services.frontend;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * <p>
 *     {@code JwtService} is dedicated to handle most of JWT-related things.
 * </p>
 */
@Service
public class JwtService {
    // Default secret for running tests
    // (if env variable is empty)
    private static final String SECRET = System.getenv("JWT_SECRET") != null
            ? System.getenv("JWT_SECRET")
            : "dGhpc2lzYXN1cGVyc2VjcmV0a2V5dGhhdGlzYXRsZWFzdDMyYnl0ZXNsb25n";

    // Secret key for validating or creating JWT tokens
    private static final SecretKey KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));

    /**
     * <p>
     *     Generates new JWT token based on user ID.
     * </p>
     *
     * @param userId user ID.
     *
     * @return JWT token.
     */
    public String generate(UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(4, ChronoUnit.HOURS)))
                .signWith(KEY)
                .compact();
    }

    /**
     * <p>
     *     Extracts user ID from JWT token.
     * </p>
     *
     * @param token JWT token
     *
     * @return user ID
     */
    public String extractUserId(String token) {
        return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload().getSubject();
    }

    /**
     * @param jwtToken any provided String instance.
     *
     * @return true if provided {@code jwtToken} is valid, false if {@code jwtToken} is not valid, or it is null.
     */
    public Boolean isValid(@Nullable String jwtToken) {
        // catching empty strings/null/whitespaces
        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            return false;
        }

        try {
            extractUserId(jwtToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // catching illegal arguments
            return false;
        }
    }
}
