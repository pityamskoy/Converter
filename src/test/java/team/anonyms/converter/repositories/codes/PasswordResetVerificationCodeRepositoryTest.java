package team.anonyms.converter.repositories.codes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.entities.codes.PasswordResetVerificationCode;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.services.frontend.AuthenticationService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.flyway.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "EMAIL_SENDER_ADDRESS=test@mail.com",
                "app.mail.from=test@mail.com",
                "JWT_SECRET=dGhpc2lzYXN1cGVyc2VjcmV0a2V5dGhhdGlzYXRsZWFzdDMyYnl0ZXNsb25n"
        }
)
@Transactional
class PasswordResetVerificationCodeRepositoryTest {

    @Autowired
    private PasswordResetVerificationCodeRepository passwordResetRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationService authenticationService;

    private User createAndSaveUser(String email) {
        User user = User.builder()
                .username("testuser")
                .email(email)
                .password("password")
                .isVerified(false)
                .build();
        return userRepository.save(user);
    }

    @Test
    void testFindByUserId_Success() {
        User savedUser = createAndSaveUser("pass1@gmail.com");

        PasswordResetVerificationCode code = PasswordResetVerificationCode.builder()
                .user(savedUser)
                .code("555555")
                .expiration(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();

        passwordResetRepository.save(code);

        Optional<PasswordResetVerificationCode> foundCode = passwordResetRepository.findByUserId(savedUser.getId());

        assertTrue(foundCode.isPresent());
        assertEquals("555555", foundCode.get().getCode());
    }

    @Test
    void testDeleteByUserId_Success() {
        User savedUser = createAndSaveUser("pass2@gmail.com");

        PasswordResetVerificationCode code = PasswordResetVerificationCode.builder()
                .user(savedUser)
                .code("666666")
                .expiration(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();

        passwordResetRepository.save(code);
        assertTrue(passwordResetRepository.findByUserId(savedUser.getId()).isPresent());

        passwordResetRepository.deleteByUserId(savedUser.getId());

        assertTrue(passwordResetRepository.findByUserId(savedUser.getId()).isEmpty());
    }

    @Test
    void testDeleteAllByExpirationBefore_Success() {
        User user1 = createAndSaveUser("expired_pass@gmail.com");
        User user2 = createAndSaveUser("valid_pass@gmail.com");

        PasswordResetVerificationCode expiredCode = PasswordResetVerificationCode.builder()
                .user(user1)
                .code("777777")
                .expiration(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
        passwordResetRepository.save(expiredCode);

        PasswordResetVerificationCode validCode = PasswordResetVerificationCode.builder()
                .user(user2)
                .code("888888")
                .expiration(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
        passwordResetRepository.save(validCode);

        passwordResetRepository.deleteAllByExpirationBefore(Instant.now());

        assertTrue(passwordResetRepository.findByUserId(user1.getId()).isEmpty());
        assertTrue(passwordResetRepository.findByUserId(user2.getId()).isPresent());
    }
}