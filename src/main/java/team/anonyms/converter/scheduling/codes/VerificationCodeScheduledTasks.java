package team.anonyms.converter.scheduling.codes;

import org.springframework.scheduling.annotation.Scheduled;
import team.anonyms.converter.entities.codes.VerificationCode;
import team.anonyms.converter.repositories.codes.VerificationCodeRepository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public abstract class VerificationCodeScheduledTasks<R extends VerificationCodeRepository<T>,
        T extends VerificationCode> {
    private final R verificationCodeRepository;

    public VerificationCodeScheduledTasks(R verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void deleteAllExpiredVerificationCodes() {
        verificationCodeRepository.deleteAllByExpirationBefore(Instant.now());
    }
}
