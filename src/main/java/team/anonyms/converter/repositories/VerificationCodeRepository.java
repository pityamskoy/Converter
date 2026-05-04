package team.anonyms.converter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.anonyms.converter.entities.VerificationCode;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    void deleteAllByExpirationBefore(Instant now);
}
