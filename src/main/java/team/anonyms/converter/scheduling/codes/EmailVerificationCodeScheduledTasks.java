package team.anonyms.converter.scheduling.codes;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.anonyms.converter.repositories.codes.EmailVerificationCodeRepository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class EmailVerificationCodeScheduledTasks {
    EmailVerificationCodeRepository repository;

    public EmailVerificationCodeScheduledTasks(EmailVerificationCodeRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void deleteAllExpiredVerificationCodes() {
        repository.deleteAllByExpirationBefore(Instant.now());
    }
}
