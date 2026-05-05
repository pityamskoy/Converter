package team.anonyms.converter.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.services.frontend.AuthenticationService;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.util.List;
import java.util.UUID;

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
class PatternRepositoryTest {

    @Autowired
    private PatternRepository patternRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationService authenticationService;

    private User createAndSaveUser() {
        User user = User.builder()
                .username("testuser")
                .email("test@gmail.com")
                .password("password")
                .isVerified(false)
                .build();
        return userRepository.save(user);
    }

    @Test
    void testFindAllByUserId_Success() {
        User savedUser = createAndSaveUser();

        Pattern pattern1 = Pattern.builder().user(savedUser).name("Pattern 1").build();
        Pattern pattern2 = Pattern.builder().user(savedUser).name("Pattern 2").build();

        patternRepository.saveAll(List.of(pattern1, pattern2));

        List<Pattern> patterns = patternRepository.findAllByUserId(savedUser.getId());

        assertEquals(2, patterns.size());
    }

    @Test
    void testDeleteAllByUserId_Success() {
        User savedUser = createAndSaveUser();

        Pattern pattern = Pattern.builder().user(savedUser).name("Pattern to delete").build();

        patternRepository.save(pattern);
        assertEquals(1, patternRepository.findAllByUserId(savedUser.getId()).size());

        patternRepository.deleteAllByUserId(savedUser.getId());

        assertEquals(0, patternRepository.findAllByUserId(savedUser.getId()).size());
    }

    @Test
    void testFindPatternById_WithNullId_ReturnsNull() {
        Pattern result = patternRepository.findPatternById(null);
        assertNull(result);
    }

    @Test
    void testFindPatternById_NotFound_ThrowsEntityNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        JpaObjectRetrievalFailureException exception = assertThrows(
                JpaObjectRetrievalFailureException.class,
                () -> patternRepository.findPatternById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("Pattern not found; id=" + nonExistentId));
    }

    @Test
    void testFindPatternById_Found_ReturnsPattern() {
        User savedUser = createAndSaveUser();

        Pattern pattern = Pattern.builder().user(savedUser).name("Existing Pattern").build();
        Pattern savedPattern = patternRepository.save(pattern);

        Pattern foundPattern = patternRepository.findPatternById(savedPattern.getId());

        assertNotNull(foundPattern);
        assertEquals("Existing Pattern", foundPattern.getName());
    }
}