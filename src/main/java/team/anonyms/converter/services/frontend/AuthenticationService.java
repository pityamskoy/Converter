package team.anonyms.converter.services.frontend;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import team.anonyms.converter.dto.service.credentials.CredentialsServiceDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.repositories.UserRepository;
import team.anonyms.converter.utility.exceptions.EmailExistsException;

import javax.security.auth.login.CredentialException;
import java.util.Optional;
import java.util.UUID;

@Service
public final class AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository, UserMapper userMapper, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }

    /**
     * @param credentials login credentials.
     *
     * @return {@link LoginResultServiceDto}.
     *
     * @throws CredentialException if {@code jwtToken}, {@code email} and {@code password} are null.
     */
    public LoginResultServiceDto login(CredentialsServiceDto credentials) throws CredentialException {
        if (credentials.jwtToken() == null && (credentials.email() == null || credentials.password() == null)) {
            throw new CredentialException("Login credentials are missing.");
        }

        if (credentials.jwtToken() != null && (credentials.email() == null || credentials.password() == null))  {
            UUID userId = UUID.fromString(jwtService.extractUserId(credentials.jwtToken()));
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isEmpty()) {
                throw new EntityNotFoundException("User not found; id=" + userId);
            }

            User user = userOptional.get();

            return new LoginResultServiceDto(
                    true,
                    user.getUsername(),
                    user.getEmail(),
                    userId,
                    credentials.jwtToken()
            );
        }

        String email = credentials.email();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; email=" + email);
        }

        User user = userOptional.get();
        if (user.getPassword().equals(credentials.password())) {
            String jwtToken = jwtService.generate(user.getId());
            return new LoginResultServiceDto(true, user.getUsername(), user.getEmail(), user.getId(), jwtToken);
        }

        return new LoginResultServiceDto(
                false,
                user.getUsername(),
                user.getEmail(),
                user.getId(),
                null
        );
    }

    public LoginResultServiceDto register(UserToRegisterServiceDto userToRegister) {
        Optional<User> userOptional = userRepository.findByEmail(userToRegister.email());
        if (userOptional.isPresent()) {
            throw new EmailExistsException("Email already exists; email=" + userToRegister.email());
        }

        User userRegistered = userMapper.userToRegisterServiceDtoToEntity(userToRegister);
        userRepository.save(userRegistered);

        return new LoginResultServiceDto(
                true,
                userRegistered.getUsername(),
                userRegistered.getEmail(),
                userRegistered.getId(),
                jwtService.generate(userRegistered.getId())
        );
    }
}
