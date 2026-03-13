package team.anonyms.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * <p>{@code Main} class is the entrypoint of the backend, which based on {@link SpringApplication}.</p>
 */
@SpringBootApplication
@ComponentScan(basePackages = {"team.anonyms.converter"})
@EntityScan(basePackages = {"team.anonyms.converter.entities"})
@EnableJpaRepositories(basePackages = {"team.anonyms.converter.repositories"})
public final class Main {
    static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}