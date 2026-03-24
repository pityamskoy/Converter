package team.anonyms.converter.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.entities.User;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// тестовая дбшка нужна, чтобы не сорить в основную -
// закидываем ее в память
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver"
})

// в рамках тестов для репозиториев написал только для этого,
// ибо лишь у userrepository есть свой уникальный метод
@Transactional
class UserRepositoryMethodTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail_Success() {
        // создаем в бдшке экземпляр юзера
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("test_user");
        user.setEmail("test@gmail.com");
        user.setPassword("МЕГАСИГМАПАРОЛЬ");
        user.setPatterns(new ArrayList<>());

        // сохраняем и вызываем метод
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test@gmail.com");

        // проверка, что юзер есть и его имя совпадает
        assertTrue(foundUser.isPresent());
        assertEquals("test_user", foundUser.get().getUsername());
    }

    @Test
    void testFindByEmail_NotFound() {
        // попытка найти несуществующего юзера
        Optional<User> foundUser = userRepository.findByEmail("not_exist@gmail.com");

        assertTrue(foundUser.isEmpty());
    }
}