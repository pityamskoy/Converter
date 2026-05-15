package team.anonyms.converter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team.anonyms.converter.services.frontend.JwtService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Import(JwtService.class)
@TestPropertySource(properties = "jwt.secret=dGhpc2lzYXN1cGVyc2VjcmV0a2V5dGhhdGlzYXRsZWFzdDMyYnl0ZXNsb25n")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void testGenerateAndExtractUserId_Success() {
        UUID originalUserId = UUID.randomUUID();

        String token = jwtService.generate(originalUserId);

        assertNotNull(token, "token can't be null");
        assertFalse(token.isEmpty(), "token can't be empty");

        String extractedId = jwtService.extractUserId(token);

        assertEquals(originalUserId.toString(), extractedId, "extracted id must be equal to original");
    }

    @Test
    void testIsValid_ValidToken_ReturnsTrue() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generate(userId);

        assertTrue(jwtService.isValid(token));
    }

    @Test
    void testIsValid_InvalidSignature_ReturnsFalse() {
        String fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvbiBEb2UiLCJpYXQiOjE1MTYyMzkwMjJ9." +
                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        assertFalse(jwtService.isValid(fakeToken));
    }

    @Test
    void testIsValid_NullOrEmptyToken_ReturnsFalse() {
        assertFalse(jwtService.isValid(null));
        assertFalse(jwtService.isValid(""));
        assertFalse(jwtService.isValid("   "));
    }
}