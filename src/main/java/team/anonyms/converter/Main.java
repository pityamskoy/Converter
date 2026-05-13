package team.anonyms.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * {@code Main} class is the entrypoint of the application, which based on {@link SpringApplication}.
 */
@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = {"team.anonyms.converter.entities"})
@EnableJpaRepositories(basePackages = {"team.anonyms.converter.repositories"})
public class Main {
    static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
