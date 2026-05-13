package team.anonyms.converter.scheduling.codes;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.anonyms.converter.repositories.codes.PasswordResetVerificationCodeRepository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class PasswordResetVerificationCodeScheduledTasks {
    PasswordResetVerificationCodeRepository repository;

    public PasswordResetVerificationCodeScheduledTasks(PasswordResetVerificationCodeRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void deleteAllExpiredVerificationCodes() {
        repository.deleteAllByExpirationBefore(Instant.now());
    }
}
