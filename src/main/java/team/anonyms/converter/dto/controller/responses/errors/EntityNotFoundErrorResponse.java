package team.anonyms.converter.dto.controller.responses.errors;

public class EntityNotFoundErrorResponse extends ErrorResponse {
    public EntityNotFoundErrorResponse(int statusCode, String message) {
        super(statusCode, message);
    }
}
