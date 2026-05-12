package team.anonyms.converter.repositories.codes;

import org.springframework.data.jpa.repository.JpaRepository;
import team.anonyms.converter.entities.codes.VerificationCode;

import java.util.Optional;
import java.util.UUID;

public interface VerificationCodeRepository<T extends VerificationCode> extends JpaRepository<T, Long> {
    Optional<T> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}