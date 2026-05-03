package team.anonyms.converter.services.frontend;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * <p>
 *     {@code JwtService} is dedicated to handle most of JWT-related things.
 * </p>
 */
@Service
public class JwtService {
    private static final String secret = System.getenv("JWT_SECRET");

    /**
     * <p>
     *     Creates secret key for validating or creating JWT token.
     * </p>
     *
     * @return key in {@link SecretKey} format.
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

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
        Date current = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(current)
                .expiration(new Date(current.getTime() + 14400000)) // 4 hours
                .signWith(key())
                .compact(); // ask
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
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Boolean isValid(String token) {
        try {
            extractUserId(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
