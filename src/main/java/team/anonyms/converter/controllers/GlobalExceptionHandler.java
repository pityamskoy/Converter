package team.anonyms.converter.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team.anonyms.converter.errors.UnsupportedExtensionException;

/**
 * <p>{@code GlobalExceptionHandler} is supposed to handle all {@link RuntimeException}, which all controllers
 * may throw.
 * </p>
 */
@RestControllerAdvice
@SuppressWarnings(value = "unused")
public final class GlobalExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
}
