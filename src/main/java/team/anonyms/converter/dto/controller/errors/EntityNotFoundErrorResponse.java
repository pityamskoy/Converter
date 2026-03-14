package team.anonyms.converter.dto.controller.errors;

public class EntityNotFoundErrorResponse extends ErrorResponse {
    public EntityNotFoundErrorResponse(int statusCode, String message) {
        super(statusCode, message);
    }
}
