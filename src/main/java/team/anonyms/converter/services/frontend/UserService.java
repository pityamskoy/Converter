package team.anonyms.converter.services.frontend;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateServiceDto;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.PatternMapper;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public final class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PatternMapper patternMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper, PatternMapper patternMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.patternMapper = patternMapper;
    }

    public UserServiceDto updateUser(UserToUpdateServiceDto userToUpdate) {
        Optional<User> userOptional = userRepository.findById(userToUpdate.id());

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + userToUpdate.id());
        }

        User userUpdated = userOptional.get();

        List<Pattern> patterns = userToUpdate.patterns().stream().
                map(patternMapper::patternServiceDtoToEntity).toList();

        userUpdated.setUsername(userToUpdate.username());
        userUpdated.setEmail(userToUpdate.email());
        userUpdated.setPassword(userToUpdate.password());
        userUpdated.setPatterns(patterns);

        userRepository.save(userUpdated);

        return userMapper.userToServiceDto(userUpdated);
    }

    public void deleteUser(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + userId);
        }

        userRepository.delete(userOptional.get());
    }
}
