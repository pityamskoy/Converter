package team.anonyms.converter.services.frontend;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateServiceDto;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.repositories.VerificationTokenRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PatternRepository patternRepository;
    private final ModificationRepository modificationRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            VerificationTokenRepository verificationTokenRepository,
            PatternRepository patternRepository,
            ModificationRepository modificationRepository,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.patternRepository = patternRepository;
        this.modificationRepository = modificationRepository;
        this.userMapper = userMapper;
    }

    public UserServiceDto updateUser(UserToUpdateServiceDto userToUpdate) {
        Optional<User> userOptional = userRepository.findById(userToUpdate.id());

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + userToUpdate.id());
        }

        User userUpdated = userOptional.get();

        userUpdated.setUsername(userToUpdate.username());
        userUpdated.setEmail(userToUpdate.email());
        userUpdated.setPassword(userToUpdate.password());

        userRepository.save(userUpdated);

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
        verificationTokenRepository.deleteByUserId(userId);
        userRepository.delete(userOptional.get());
    }
}
