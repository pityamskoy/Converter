package team.anonyms.converter.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team.anonyms.converter.dto.controller.responses.errors.CredentialExceptionErrorResponse;
import team.anonyms.converter.dto.controller.responses.errors.EmailExistsExceptionErrorResponse;
import team.anonyms.converter.dto.controller.responses.errors.IllegalPatternExceptionErrorResponse;
import team.anonyms.converter.utility.annotations.LastSupportedProjectVersion;
import team.anonyms.converter.utility.exceptions.EmailExistsException;
import team.anonyms.converter.utility.exceptions.IllegalPatternException;
import team.anonyms.converter.utility.exceptions.UnsupportedExtensionException;

import javax.security.auth.login.CredentialException;

import static team.anonyms.converter.utility.enums.ProjectVersion.RELEASE_0;

/**
 * <p>
 *     {@code GlobalExceptionHandler} is supposed to handle all {@link RuntimeException}, which all controllers may throw.
 * </p>
 */
@LastSupportedProjectVersion(RELEASE_0)
@RestControllerAdvice
@SuppressWarnings(value = "unused")
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(exception = CredentialException.class)
    public ResponseEntity<CredentialExceptionErrorResponse> handleCredentialException(CredentialException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(new CredentialExceptionErrorResponse());
    }

    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = UnsupportedExtensionException.class)
    public ResponseEntity<Void> handleUnsupportedExtensionException(UnsupportedExtensionException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(exception = NullPointerException.class)
    public ResponseEntity<Void> handleNullPointerException(NullPointerException e) {
        log.error(e.getMessage());
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(exception = IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(exception = IllegalPatternException.class)
    public ResponseEntity<IllegalPatternExceptionErrorResponse> handleIllegalPatternException(
            IllegalPatternException e
    ) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(new IllegalPatternExceptionErrorResponse());
    }

    @ExceptionHandler(exception = EmailExistsException.class)
    public ResponseEntity<EmailExistsExceptionErrorResponse> handleEmailExistsException(EmailExistsException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(new EmailExistsExceptionErrorResponse());
    }
}
