package team.anonyms.converter.dto.controller.responses.errors;

public final class CredentialExceptionErrorResponse extends ErrorResponse {
    private static final int STATUS_CODE = 400;
    private static final String DEFAULT_MESSAGE = "CREDENTIAL";

    public CredentialExceptionErrorResponse() {
        super(STATUS_CODE, DEFAULT_MESSAGE);
    }
}
