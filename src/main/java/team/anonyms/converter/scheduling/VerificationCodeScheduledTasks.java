package team.anonyms.converter.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.anonyms.converter.repositories.VerificationCodeRepository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class VerificationCodeScheduledTasks {
    private final VerificationCodeRepository verificationCodeRepository;

    public VerificationCodeScheduledTasks(VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void deleteAllExpiredVerificationCodes() {
        verificationCodeRepository.deleteAllByExpirationBefore(Instant.now());
    }
}
