package team.anonyms.converter.services.frontend;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.dto.service.authentication.AuthenticationServiceDto;
import team.anonyms.converter.dto.service.authentication.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.*;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.repositories.codes.EmailVerificationCodeRepository;
import team.anonyms.converter.exceptions.email.EmailAlreadyExistsException;
import team.anonyms.converter.repositories.codes.PasswordResetVerificationCodeRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final JwtService jwtService;
    private final EmailService emailService;

    private final UserRepository userRepository;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final PasswordResetVerificationCodeRepository passwordResetVerificationCodeRepository;
    private final PatternRepository patternRepository;
    private final ModificationRepository modificationRepository;

    private final UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            JwtService jwtService,
            EmailService emailService,
            UserRepository userRepository,
            EmailVerificationCodeRepository emailVerificationCodeRepository,
            PasswordResetVerificationCodeRepository passwordResetVerificationCodeRepository,
            PatternRepository patternRepository,
            ModificationRepository modificationRepository,
            UserMapper userMapper,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.jwtService = jwtService;
        this.emailService = emailService;

        this.userRepository = userRepository;
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
        this.passwordResetVerificationCodeRepository = passwordResetVerificationCodeRepository;
        this.patternRepository = patternRepository;
        this.modificationRepository = modificationRepository;

        this.userMapper = userMapper;

        this.passwordEncoder = passwordEncoder;
    }

    public AuthenticationServiceDto register(UserToRegisterServiceDto userToRegister) {
        Optional<User> userOptional = userRepository.findByEmail(userToRegister.email());
        if (userOptional.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists; email=" + userToRegister.email());
        }

        User userRegistered = userMapper.userToRegisterServiceDtoToEntity(userToRegister);
        userRegistered.setPassword(passwordEncoder.encode(userToRegister.password()));
        userRegistered = userRepository.save(userRegistered);

        LoginResultServiceDto result = new LoginResultServiceDto(
                true,
                userRegistered.getId(),
                userRegistered.getUsername(),
                userRegistered.getEmail(),
                userRegistered.getIsVerified()
        );

        return new AuthenticationServiceDto(result, jwtService.generate(userRegistered.getId()));
    }

    public UserServiceDto updateUser(UserToUpdateServiceDto userToUpdate, UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + userId);
        }

        User userUpdated = userOptional.get();
        userUpdated.setUsername(userToUpdate.username());
        userUpdated.setPassword(passwordEncoder.encode(userToUpdate.password()));

        userRepository.save(userUpdated);

        return userMapper.userToServiceDto(userUpdated);
    }

    public UserServiceDto updateEmail(String email, UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + userId);
        }

        Optional<User> userWithExistedEmail = userRepository.findByEmail(email);
        if (userWithExistedEmail.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists; email=" + email);
        }

        User userUpdated = userOptional.get();
        userUpdated.setEmail(email);
        userUpdated.setIsVerified(false);

        userRepository.save(userUpdated);
        emailService.sendEmailVerificationCode(userUpdated);

        return userMapper.userToServiceDto(userUpdated);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + userId);
        }

        modificationRepository.deleteAllByUserId(userId);
        patternRepository.deleteAllByUserId(userId);
        emailVerificationCodeRepository.deleteByUserId(userId);
        passwordResetVerificationCodeRepository.deleteByUserId(userId);
        userRepository.delete(userOptional.get());
    }
}
