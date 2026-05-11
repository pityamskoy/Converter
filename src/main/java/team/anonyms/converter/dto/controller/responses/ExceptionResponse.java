package team.anonyms.converter.dto.controller.responses;

import lombok.Data;

import java.time.Instant;

@Data
public class ExceptionResponse {
    private int statusCode;
    private String message;
    private Instant time;

    public ExceptionResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.time = Instant.now();
    }
}
