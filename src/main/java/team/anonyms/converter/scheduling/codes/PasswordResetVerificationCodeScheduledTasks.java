package team.anonyms.converter.scheduling.codes;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.anonyms.converter.repositories.codes.PasswordResetVerificationCodeRepository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class PasswordResetVerificationCodeScheduledTasks {
    PasswordResetVerificationCodeRepository passwordResetVerificationCodeRepository;

    public PasswordResetVerificationCodeScheduledTasks(
            PasswordResetVerificationCodeRepository passwordResetVerificationCodeRepository
    ) {
        this.passwordResetVerificationCodeRepository = passwordResetVerificationCodeRepository;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void deleteAllExpiredVerificationCodes() {
        passwordResetVerificationCodeRepository.deleteAllByExpirationBefore(Instant.now());
    }
}
