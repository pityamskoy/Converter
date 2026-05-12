package team.anonyms.converter.repositories.codes;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.entities.codes.EmailVerificationCode;

import java.time.Instant;

@Repository
public interface EmailVerificationCodeRepository extends VerificationCodeRepository<EmailVerificationCode> {
    @Transactional
    @Modifying
    @Query("DELETE FROM EmailVerificationCode vc WHERE vc.expiration < :now")
    void deleteAllByExpirationBefore(Instant now);
}
