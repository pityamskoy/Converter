package team.anonyms.converter.scheduling.codes;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.anonyms.converter.repositories.codes.EmailVerificationCodeRepository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class EmailVerificationCodeScheduledTasks {
    EmailVerificationCodeRepository emailVerificationCodeRepository;

    public EmailVerificationCodeScheduledTasks(EmailVerificationCodeRepository emailVerificationCodeRepository) {
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void deleteAllExpiredVerificationCodes() {
        emailVerificationCodeRepository.deleteAllByExpirationBefore(Instant.now());
    }
}
