package team.anonyms.converter.repositories.codes;

import org.springframework.stereotype.Repository;
import team.anonyms.converter.entities.codes.PasswordResetVerificationCode;

@Repository
public interface PasswordResetVerificationCodeRepository extends
        VerificationCodeRepository<PasswordResetVerificationCode> {
}
