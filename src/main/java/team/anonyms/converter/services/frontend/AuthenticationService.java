package team.anonyms.converter.services.frontend;

import jakarta.persistence.EntityNotFoundException;
import org.antlr.v4.runtime.misc.Pair;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team.anonyms.converter.dto.service.credentials.CredentialsServiceDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.entities.codes.EmailVerificationCode;
import team.anonyms.converter.entities.codes.PasswordResetVerificationCode;
import team.anonyms.converter.repositories.codes.EmailVerificationCodeRepository;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.repositories.codes.PasswordResetVerificationCodeRepository;

import javax.security.auth.login.CredentialException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final PasswordResetVerificationCodeRepository passwordResetVerificationCodeRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationService(
            JwtService jwtService,
            UserRepository userRepository,
            EmailVerificationCodeRepository emailVerificationCodeRepository,
            PasswordResetVerificationCodeRepository passwordResetVerificationCodeRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
        this.passwordResetVerificationCodeRepository = passwordResetVerificationCodeRepository;

        this.passwordEncoder = passwordEncoder;
    }

    public Boolean isVerified(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; userId=" + userId);
        }

        return userOptional.get().getIsVerified();
    }

    /**
     * @param credentials login credentials.
     * @param jwtToken jwtToken extracted from cookie
     *
     * @return {@link Pair} where A is the result of login, B is jwtToken.
     *
     * @throws CredentialException if {@code jwtToken}, {@code email} and {@code password} are null.
     */
    public Pair<LoginResultServiceDto, String> login(
            CredentialsServiceDto credentials,
            @Nullable String jwtToken
    ) throws CredentialException {
        if (jwtToken == null && (credentials.email() == null || credentials.password() == null)) {
            throw new CredentialException("Login credentials are missing.");
        }

        if (jwtToken != null && jwtService.isValid(jwtToken))  {
            UUID userId = UUID.fromString(jwtService.extractUserId(jwtToken));

            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new EntityNotFoundException("User not found; id=" + userId);
            }

            User user = userOptional.get();
            LoginResultServiceDto result = new LoginResultServiceDto(true, user.getUsername(), user.getEmail(), userId);
            return new Pair<>(result, jwtToken);
        }

        String email = credentials.email();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; email=" + email);
        }

        User user = userOptional.get();
        if (passwordEncoder.matches(credentials.password(), user.getPassword())) {
            String newJwtToken = jwtService.generate(user.getId());
            LoginResultServiceDto result = new LoginResultServiceDto(
                    true,
                    user.getUsername(),
                    user.getEmail(),
                    user.getId()
            );

            return new Pair<>(result, newJwtToken);
        }

        LoginResultServiceDto result = new LoginResultServiceDto(
                false,
                null,
                null,
                null
        );

        return new Pair<>(result, jwtToken);
    }

    public Boolean verifyEmail(UUID userId, String emailVerificationCode) {
        Optional<EmailVerificationCode> emailVerificationCodeOptional =
                emailVerificationCodeRepository.findByUserId(userId);
        if (emailVerificationCodeOptional.isEmpty()) {
            throw new EntityNotFoundException("Email verification code not found; userId=" + userId);
        }

        EmailVerificationCode actualEmailVerificationCode = emailVerificationCodeOptional.get();
        if (emailVerificationCode.equals(actualEmailVerificationCode.getCode())
                && actualEmailVerificationCode.getExpiration().isAfter(Instant.now())) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new EntityNotFoundException("User not found; userId=" + userId);
            }

            User user = userOptional.get();
            user.setIsVerified(true);

            userRepository.save(user);
            emailVerificationCodeRepository.delete(actualEmailVerificationCode);

            return true;
        } else {
            return false;
        }
    }

    public Boolean verifyPasswordReset(UUID userId, String passwordResetVerificationCode) {
        Optional<PasswordResetVerificationCode> passwordResetVerificationCodeOptional =
                passwordResetVerificationCodeRepository.findByUserId(userId);
        if (passwordResetVerificationCodeOptional.isEmpty()) {
            throw new EntityNotFoundException("Password reset verification code not found; userId=" + userId);
        }

        PasswordResetVerificationCode actualPasswordResetVerificationCode = passwordResetVerificationCodeOptional.get();
        if (passwordResetVerificationCode.equals(actualPasswordResetVerificationCode.getCode())
                && actualPasswordResetVerificationCode.getExpiration().isAfter(Instant.now())) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new EntityNotFoundException("User not found; userId=" + userId);
            }

            User user = userOptional.get();
            user.setIsVerified(true);

            userRepository.save(user);
            passwordResetVerificationCodeRepository.delete(actualPasswordResetVerificationCode);

            return true;
        } else {
            return false;
        }
    }
}
