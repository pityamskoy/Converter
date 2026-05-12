package team.anonyms.converter.repositories.codes;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.entities.codes.PasswordResetVerificationCode;

import java.time.Instant;

@Repository
public interface PasswordResetVerificationCodeRepository extends
        VerificationCodeRepository<PasswordResetVerificationCode> {
    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetVerificationCode vc WHERE vc.expiration < :now")
    void deleteAllByExpirationBefore(Instant now);
}
