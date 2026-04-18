package team.anonyms.converter.dto.controller.responses.errors;

public final class CredentialExceptionErrorResponse extends ErrorResponse {
    private final static int STATUS_CODE = 400;
    private final static String DEFAULT_MESSAGE = "CREDENTIALS";

    public CredentialExceptionErrorResponse(int statusCode, String message) {
        super(statusCode, message);
    }
}
