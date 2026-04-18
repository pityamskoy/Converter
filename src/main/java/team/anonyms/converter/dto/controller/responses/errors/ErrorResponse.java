package team.anonyms.converter.dto.controller.responses.errors;

import lombok.Data;

import java.time.Instant;

@Data
public abstract class ErrorResponse {
    private int statusCode;
    private String message;
    private Instant time;

    protected ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.time = Instant.now();
    }
}
