package team.anonyms.converter.services.frontend;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Service;
import team.anonyms.converter.controllers.frontend.AuthenticationController;
import team.anonyms.converter.dto.service.credentials.CredentialsServiceDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.repositories.UserRepository;

import javax.security.auth.login.CredentialException;
import java.util.Optional;
import java.util.UUID;

@Service
public final class AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthenticationService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * @param userId a value of cookie, which {@link AuthenticationController} accepts as an argument.
     * @param credentials login credentials.
     *
     * @return {@link Pair}<{@link Cookie}, {@link LoginResultServiceDto}>, where {@link Cookie} is null if {@code User}
     * has been found by {@code userId}.
     *
     * @throws CredentialException if cookie is null, and credentials are null.
     */
    public Pair<Cookie, LoginResultServiceDto> login(
            @Nullable String userId,
            CredentialsServiceDto credentials
    ) throws CredentialException {
        if (userId == null && (credentials.email() == null || credentials.password() == null)) {
            throw new CredentialException("Login credentials are missing.");
        }

        if (userId != null && (credentials.email() == null || credentials.password() == null))  {
            Optional<User> userOptional = userRepository.findById(UUID.fromString(userId));

            if (userOptional.isEmpty()) {
                throw new EntityNotFoundException("User not found; id=" + userId);
            }

            User user = userOptional.get();

            return new Pair<>(null,
                    new LoginResultServiceDto(true, user.getUsername(), UUID.fromString(userId)));
        }

        String email = credentials.email();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; email=" + email);
        }

        User user = userOptional.get();
        LoginResultServiceDto loginResultDto = new LoginResultServiceDto(user.getPassword().
                equals(credentials.password()), user.getUsername(), user.getId());

        if (loginResultDto.success()) {
            Cookie cookie = new Cookie("user_id", loginResultDto.userId().toString());
            cookie.setPath("/");
            cookie.setMaxAge(14400);
            cookie.setHttpOnly(false);
            cookie.setSecure(false);

            return new Pair<>(cookie, loginResultDto);
        }

        return new Pair<>(null, loginResultDto);
    }

    public Pair<UserServiceDto, Cookie> register(UserToRegisterServiceDto userToRegister) {
        User userRegistered = userMapper.userToRegisterServiceDtoToEntity(userToRegister);
        userRepository.save(userRegistered);

        Cookie cookie = new Cookie("user_id", userRegistered.getId().toString());
        cookie.setPath("/");
        cookie.setMaxAge(14400);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);

        return new Pair<>(userMapper.userToServiceDto(userRegistered), cookie);
    }

    public Cookie logout(String userId) {
        Cookie cookie = new Cookie("user_id", userId);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);

        return cookie;
    }
}
