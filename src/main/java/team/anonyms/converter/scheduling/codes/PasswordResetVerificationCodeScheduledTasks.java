package team.anonyms.converter.scheduling.codes;

import org.springframework.stereotype.Component;
import team.anonyms.converter.entities.codes.PasswordResetVerificationCode;
import team.anonyms.converter.repositories.codes.PasswordResetVerificationCodeRepository;

@Component
public class PasswordResetVerificationCodeScheduledTasks extends
        VerificationCodeScheduledTasks<PasswordResetVerificationCodeRepository, PasswordResetVerificationCode> {

    public PasswordResetVerificationCodeScheduledTasks(PasswordResetVerificationCodeRepository verificationCodeRepository) {
        super(verificationCodeRepository);
    }
}
