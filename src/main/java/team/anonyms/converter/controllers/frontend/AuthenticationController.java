package team.anonyms.converter.controllers.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.dto.controller.credentials.CredentialsControllerDto;
import team.anonyms.converter.dto.controller.credentials.LoginResultControllerDto;
import team.anonyms.converter.dto.controller.user.UserToRegisterControllerDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.mappers.CredentialsMapper;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.services.frontend.AuthenticationService;

import javax.security.auth.login.CredentialException;

@RestController
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
            @RequestBody CredentialsControllerDto credentials
    ) throws CredentialException {
        log.info("Called login");

        LoginResultServiceDto result = authenticationService.login(
                credentialsMapper.credentialsControllerDtoToService(credentials));

        return ResponseEntity.ok(credentialsMapper.loginResultServiceDtoToController(result));
    }

    @PostMapping("/registration")
    public ResponseEntity<LoginResultControllerDto> register(@RequestBody UserToRegisterControllerDto userToRegister) {
        log.info("Called register");

        LoginResultControllerDto result = credentialsMapper.loginResultServiceDtoToController(
                authenticationService.register(userMapper.userToRegisterControllerDtoToService(userToRegister)));

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
