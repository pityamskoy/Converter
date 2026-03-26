package team.anonyms.converter.controllers.frontend;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.misc.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.dto.controller.credentials.CredentialsControllerDto;
import team.anonyms.converter.dto.controller.credentials.LoginResultControllerDto;
import team.anonyms.converter.dto.controller.user.UserControllerDto;
import team.anonyms.converter.dto.controller.user.UserToRegisterControllerDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.mappers.CredentialsMapper;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.services.frontend.AuthenticationService;

import javax.security.auth.login.CredentialException;

@RestController
@CrossOrigin(origins = {"http://localhost:4200"}, allowCredentials = "true")
@RequestMapping("/auth")
public final class AuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;
    private final CredentialsMapper credentialsMapper;
    private final UserMapper userMapper;

    public AuthenticationController(
            AuthenticationService authenticationService,
            CredentialsMapper credentialsMapper,
            UserMapper userMapper
    ) {
        this.authenticationService = authenticationService;
        this.credentialsMapper = credentialsMapper;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResultControllerDto> login(
            @CookieValue(required = false, value = "user_id") String userId,
            @RequestBody CredentialsControllerDto credentials,
            HttpServletResponse response
    ) {
        log.info("Called login; user_id={}, credentials={}", userId, credentials);

        try {
            Pair<Cookie, LoginResultServiceDto> result = authenticationService.login(userId,
                    credentialsMapper.credentialsControllerDtoToService(credentials));

            LoginResultControllerDto loginResult = credentialsMapper.loginResultServiceDtoToController(result.b);
            if (result.a != null && loginResult.success()) {
                response.addCookie(result.a);
            }

            return ResponseEntity.ok(loginResult);
        } catch (CredentialException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<UserControllerDto> register(
            @RequestBody UserToRegisterControllerDto userToRegister,
            HttpServletResponse response
    ) {
        log.info("Called register; userToRegister={}", userToRegister);

        Pair<UserServiceDto, Cookie> result = authenticationService.register(userMapper.
                userToRegisterControllerDtoToService(userToRegister));

        response.addCookie(result.b);
        UserControllerDto userRegistered = userMapper.userServiceDtoToControllerDto(result.a);

        return ResponseEntity.status(HttpStatus.CREATED).body(userRegistered);
    }

    @DeleteMapping
    public ResponseEntity<Void> logout(
            @CookieValue(value = "user_id") String userId,
            HttpServletResponse response
    ) {
        Cookie cookie = authenticationService.logout(userId);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }
}
