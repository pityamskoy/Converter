package team.anonyms.converter.services.frontend;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.entities.VerificationCode;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.repositories.VerificationCodeRepository;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailService {
    // Symbols for generating a random verification code
    private static final String SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final JavaMailSender mailSender;

    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository userRepository;

    @Value("${app.mail.from}")
    private String emailFrom;

    public EmailService(
            JavaMailSender javaMailSender,
            VerificationCodeRepository verificationCodeRepository,
            UserRepository userRepository
    ) {
        this.mailSender = javaMailSender;

        this.verificationCodeRepository = verificationCodeRepository;
        this.userRepository = userRepository;
    }

    private String generateAndSaveVerificationCode(User user) {
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
        }

        String codeString = code.toString();

        VerificationCode verificationCode = VerificationCode.builder()
                .code(codeString)
                .expiration(Instant.now().plus(15, ChronoUnit.MINUTES))
                .user(user)
                .build();

        verificationCodeRepository.save(verificationCode);

        return codeString;
    }

    public void sendVerificationCode(User receiver) {
        Optional<VerificationCode> verificationCodeOptional = verificationCodeRepository.findByUserId(receiver.getId());
        verificationCodeOptional.ifPresent(verificationCodeRepository::delete);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(receiver.getEmail());
        mailMessage.setSubject("Verification code for CSON");
        mailMessage.setText("Your code is: " + generateAndSaveVerificationCode(receiver));

        mailSender.send(mailMessage);
    }

    public void sendVerificationCode(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + userId);
        }

        sendVerificationCode(userOptional.get());
    }
}
