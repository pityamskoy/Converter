package team.anonyms.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * <p>
 *     {@code Main} class is the entrypoint of the backend, which based on {@link SpringApplication}.
 * </p>
 */
@SpringBootApplication
@EntityScan(basePackages = {"team.anonyms.converter.entities"})
@EnableJpaRepositories(basePackages = {"team.anonyms.converter.repositories"})
public class Main {
    static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
