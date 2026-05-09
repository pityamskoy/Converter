package team.anonyms.converter.controllers.frontend;

import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.misc.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.dto.controller.credentials.CredentialsControllerDto;
import team.anonyms.converter.dto.controller.credentials.LoginResultControllerDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.mappers.CredentialsMapper;
import team.anonyms.converter.services.frontend.AuthenticationService;
import team.anonyms.converter.services.frontend.EmailService;

import javax.security.auth.login.CredentialException;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@SuppressWarnings(value = {"DataFlowIssue"})
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final CredentialsMapper credentialsMapper;

    public AuthenticationController(
            AuthenticationService authenticationService,
            EmailService emailService,
            CredentialsMapper credentialsMapper
    ) {
        this.authenticationService = authenticationService;
        this.emailService = emailService;
        this.credentialsMapper = credentialsMapper;
    }

    @PostMapping
    public ResponseEntity<LoginResultControllerDto> login(
            @RequestBody CredentialsControllerDto credentials,
            @CookieValue(value = "jwtToken", required = false) String jwtToken,
            HttpServletResponse response
    ) throws CredentialException {
        logger.info("Called login");

        Pair<LoginResultServiceDto, String> result = authenticationService.login(
                credentialsMapper.credentialsControllerDtoToService(credentials),
                jwtToken
        );

        ResponseCookie responseCookie = ResponseCookie.from("jwtToken", result.b)
                .path("/")
                .maxAge(14400)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ResponseEntity.ok(credentialsMapper.loginResultServiceDtoToController(result.a));
    }

    @PostMapping("/email/resending")
    public ResponseEntity<Void> resendVerificationCode() {
        logger.info("Called resendVerificationCode");

        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        emailService.sendEmailVerificationCode(userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email/verification")
    public ResponseEntity<Boolean> verifyEmail(@RequestBody String verificationCode) {
        logger.info("Called verifyEmail");

        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(authenticationService.verifyEmail(userId, verificationCode));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> sendPasswordResetVerificationCode() {
        logger.info("Called sendVerificationCodeForPasswordReset");

        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        emailService.sendPasswordResetVerificationCode(userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/verification")
    public ResponseEntity<Boolean> verifyPasswordReset(@RequestBody String verificationCode) {
        logger.info("Called verifyPasswordReset");

        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(authenticationService.verifyPasswordReset(userId, verificationCode));
    }

    @DeleteMapping
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        logger.info("Called logout");

        ResponseCookie responseCookie = ResponseCookie.from("jwtToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ResponseEntity.ok().build();
    }
}
