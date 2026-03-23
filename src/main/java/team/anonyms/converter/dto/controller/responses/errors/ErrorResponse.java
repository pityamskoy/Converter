package team.anonyms.converter.dto.controller.responses.errors;

import lombok.Data;

import java.time.Instant;

@Data
public abstract class ErrorResponse {
    protected int statusCode;
    protected Instant time;
    protected String message;

    protected ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.time = Instant.now();
    }
}
