package team.anonyms.converter.scheduling.codes;

import org.springframework.stereotype.Component;
import team.anonyms.converter.entities.codes.EmailVerificationCode;
import team.anonyms.converter.repositories.codes.EmailVerificationCodeRepository;

@Component
public class EmailVerificationCodeScheduledTasks extends
        VerificationCodeScheduledTasks<EmailVerificationCodeRepository, EmailVerificationCode> {

    public EmailVerificationCodeScheduledTasks(EmailVerificationCodeRepository verificationCodeRepository) {
        super(verificationCodeRepository);
    }
}
