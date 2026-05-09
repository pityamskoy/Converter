package team.anonyms.converter.services.frontend;

import jakarta.persistence.EntityNotFoundException;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateEmailServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateServiceDto;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.repositories.codes.EmailVerificationCodeRepository;
import team.anonyms.converter.exceptions.EmailExistsException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final JwtService jwtService;
    private final EmailService emailService;

    private final UserRepository userRepository;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final PatternRepository patternRepository;
    private final ModificationRepository modificationRepository;

    private final UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            JwtService jwtService,
            EmailService emailService,
            UserRepository userRepository,
            EmailVerificationCodeRepository emailVerificationCodeRepository,
            PatternRepository patternRepository,
            ModificationRepository modificationRepository,
            UserMapper userMapper,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.jwtService = jwtService;
        this.emailService = emailService;

        this.userRepository = userRepository;
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
        this.patternRepository = patternRepository;
        this.modificationRepository = modificationRepository;

        this.userMapper = userMapper;

        this.passwordEncoder = passwordEncoder;
    }

    public Pair<LoginResultServiceDto, String> register(UserToRegisterServiceDto userToRegister) {
        Optional<User> userOptional = userRepository.findByEmail(userToRegister.email());
        if (userOptional.isPresent()) {
            throw new EmailExistsException("Email already exists; email=" + userToRegister.email());
        }

        User userRegistered = userMapper.userToRegisterServiceDtoToEntity(userToRegister);
        userRegistered.setPassword(passwordEncoder.encode(userToRegister.password()));
        userRegistered = userRepository.save(userRegistered);

        emailService.sendEmailVerificationCode(userRegistered);

        LoginResultServiceDto result = new LoginResultServiceDto(
                true,
                userRegistered.getUsername(),
                userRegistered.getEmail(),
                userRegistered.getId()
        );

        return new Pair<>(result, jwtService.generate(userRegistered.getId()));
    }

    public UserServiceDto updateUser(UserToUpdateServiceDto userToUpdate) {
        Optional<User> userOptional = userRepository.findById(userToUpdate.id());
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + userToUpdate.id());
        }

        User userUpdated = userOptional.get();
        userUpdated.setUsername(userToUpdate.username());
        userUpdated.setPassword(passwordEncoder.encode(userToUpdate.password()));

        userRepository.save(userUpdated);

        return userMapper.userToServiceDto(userUpdated);
    }

    public UserServiceDto updateEmail(UserToUpdateEmailServiceDto userToUpdateEmail) {
        Optional<User> userOptional = userRepository.findById(userToUpdateEmail.id());
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + userToUpdateEmail.id());
        }

        User userUpdated = userOptional.get();
        userUpdated.setEmail(userToUpdateEmail.email());
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

        List<Pattern> patternsToDelete = patternRepository.findAllByUserId(userId);
        for (Pattern pattern : patternsToDelete) {
            modificationRepository.deleteAllByPatternId(pattern.getId());
        }

        patternRepository.deleteAllByUserId(userId);
        emailVerificationCodeRepository.deleteByUserId(userId);
        userRepository.delete(userOptional.get());
    }
}
