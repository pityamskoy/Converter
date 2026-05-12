package team.anonyms.converter.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team.anonyms.converter.dto.controller.responses.ExceptionResponse;
import team.anonyms.converter.exceptions.email.EmailAlreadyVerifiedException;
import team.anonyms.converter.exceptions.email.EmailExistsException;
import team.anonyms.converter.exceptions.IllegalPatternException;
import team.anonyms.converter.exceptions.UnsupportedExtensionException;

import javax.security.auth.login.CredentialException;

/**
 * <p>
 *     {@code GlobalExceptionHandler} is supposed to handle all {@link RuntimeException}, which all controllers may throw.
 * </p>
 */
@RestControllerAdvice
@SuppressWarnings(value = "unused")
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(exception = CredentialException.class)
    public ResponseEntity<ExceptionResponse> handleCredentialException(CredentialException e) {
        logger.error(e.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponse(400, "CREDENTIAL"));
    }

    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        logger.error(e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = UnsupportedExtensionException.class)
    public ResponseEntity<Void> handleUnsupportedExtensionException(UnsupportedExtensionException e) {
        logger.error(e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(exception = NullPointerException.class)
    public ResponseEntity<Void> handleNullPointerException(NullPointerException e) {
        logger.error("Unexpected NullPointerException", e);
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(exception = IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error(e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(exception = IllegalPatternException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalPatternException(
            IllegalPatternException e
    ) {
        logger.error(e.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponse(400, "PATTERN"));
    }

    @ExceptionHandler(exception = EmailExistsException.class)
    public ResponseEntity<ExceptionResponse> handleEmailExistsException(EmailExistsException e) {
        logger.error(e.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponse(400, "EMAIL EXISTS"));
    }

    @ExceptionHandler(exception = EmailAlreadyVerifiedException.class)
    public ResponseEntity<ExceptionResponse> handleEmailAlreadyVerifiedException(EmailAlreadyVerifiedException e) {
        logger.error(e.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponse(400, "EMAIL ALREADY VERIFIED"));
    }

    @ExceptionHandler(exception = AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException e) {
        logger.error(e.getMessage());
        return ResponseEntity.status(403).body(new ExceptionResponse(403, "ACCESS DENIED"));
    }
}
