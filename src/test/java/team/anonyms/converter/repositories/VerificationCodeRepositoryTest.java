package team.anonyms.converter.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.entities.VerificationCode;
import team.anonyms.converter.services.frontend.AuthenticationService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
class VerificationCodeRepositoryTest {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

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
        User savedUser = createAndSaveUser("test1@gmail.com");

        VerificationCode code = VerificationCode.builder()
                .user(savedUser)
                .code("123456")
                .expiration(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();

        verificationCodeRepository.save(code);

        Optional<VerificationCode> foundCode = verificationCodeRepository.findByUserId(savedUser.getId());

        assertTrue(foundCode.isPresent());
        assertEquals("123456", foundCode.get().getCode());
    }

    @Test
    void testDeleteByUserId_Success() {
        User savedUser = createAndSaveUser("test2@gmail.com");

        VerificationCode code = VerificationCode.builder()
                .user(savedUser)
                .code("654321")
                .expiration(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();

        verificationCodeRepository.save(code);
        assertTrue(verificationCodeRepository.findByUserId(savedUser.getId()).isPresent());

        verificationCodeRepository.deleteByUserId(savedUser.getId());

        assertTrue(verificationCodeRepository.findByUserId(savedUser.getId()).isEmpty());
    }

    @Test
    void testDeleteAllByExpirationBefore_Success() {
        User user1 = createAndSaveUser("expired@gmail.com");
        User user2 = createAndSaveUser("valid@gmail.com");

        VerificationCode expiredCode = VerificationCode.builder()
                .user(user1)
                .code("111111")
                .expiration(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
        verificationCodeRepository.save(expiredCode);

        VerificationCode validCode = VerificationCode.builder()
                .user(user2)
                .code("222222")
                .expiration(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
        verificationCodeRepository.save(validCode);

        verificationCodeRepository.deleteAllByExpirationBefore(Instant.now());

        assertTrue(verificationCodeRepository.findByUserId(user1.getId()).isEmpty());
        assertTrue(verificationCodeRepository.findByUserId(user2.getId()).isPresent());
    }
}