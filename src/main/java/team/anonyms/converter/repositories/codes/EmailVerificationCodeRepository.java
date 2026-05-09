package team.anonyms.converter.repositories.codes;

import org.springframework.stereotype.Repository;
import team.anonyms.converter.entities.codes.EmailVerificationCode;

@Repository
public interface EmailVerificationCodeRepository extends VerificationCodeRepository<EmailVerificationCode> {
}
