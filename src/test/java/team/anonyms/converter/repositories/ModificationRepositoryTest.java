package team.anonyms.converter.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.entities.Modification;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.services.frontend.AuthenticationService;

import java.util.List;

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
class ModificationRepositoryTest {

    @Autowired
    private ModificationRepository modificationRepository;

    @Autowired
    private PatternRepository patternRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationService authenticationService;

    private Pattern createAndSavePattern() {
        User user = User.builder()
                .username("testuser")
                .email("test@gmail.com")
                .password("password")
                .isVerified(false)
                .build();
        User savedUser = userRepository.save(user);

        Pattern pattern = Pattern.builder()
                .user(savedUser)
                .name("Base Pattern")
                .build();
        return patternRepository.save(pattern);
    }

    @Test
    void testFindAllByPatternId_Success() {
        Pattern savedPattern = createAndSavePattern();

        Modification mod1 = Modification.builder()
                .pattern(savedPattern)
                .oldName("age")
                .newName("years_old")
                .build();

        Modification mod2 = Modification.builder()
                .pattern(savedPattern)
                .oldName("name")
                .newName("nickname")
                .build();

        modificationRepository.saveAll(List.of(mod1, mod2));

        List<Modification> modifications = modificationRepository.findAllByPatternId(savedPattern.getId());

        assertEquals(2, modifications.size());
    }

    @Test
    void testDeleteAllByPatternId_Success() {
        Pattern savedPattern = createAndSavePattern();

        Modification mod = Modification.builder()
                .pattern(savedPattern)
                .oldName("value")
                .build();

        modificationRepository.save(mod);
        assertEquals(1, modificationRepository.findAllByPatternId(savedPattern.getId()).size());

        modificationRepository.deleteAllByPatternId(savedPattern.getId());

        assertEquals(0, modificationRepository.findAllByPatternId(savedPattern.getId()).size());
    }
}