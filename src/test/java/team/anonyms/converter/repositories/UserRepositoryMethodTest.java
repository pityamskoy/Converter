package team.anonyms.converter.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.services.frontend.AuthenticationService;
import team.anonyms.converter.services.frontend.JwtService;

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
                "app.mail.from=test@mail.com"
        }
)

@Transactional
class UserRepositoryMethodTest {

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void testFindByEmail_Success() {
        User user = User.builder()
                .username("test_user")
                .email("test@gmail.com")
                .password("МЕГАСИГМАПАРОЛЬ")
                .isVerified(false)
                .build();

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test@gmail.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test_user", foundUser.get().getUsername());
    }

    @Test
    void testFindByEmail_NotFound() {
        Optional<User> foundUser = userRepository.findByEmail("not_exist@gmail.com");

        assertTrue(foundUser.isEmpty());
    }
}