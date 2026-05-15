package team.anonyms.converter.dto.controller.responses;

import lombok.Data;

import java.time.Instant;

@Data
public class ExceptionResponse {
    private String message;
    private Instant time;

    public ExceptionResponse(String message) {
        this.message = message;
        this.time = Instant.now();
    }
}
