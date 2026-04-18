package team.anonyms.converter.dto.controller.responses.errors;

public final class EmailExistsExceptionErrorResponse extends ErrorResponse {
    private static final int STATUS_CODE = 400;
    private static final String DEFAULT_MESSAGE = "EMAIL EXISTS";

    public EmailExistsExceptionErrorResponse() {
        super(STATUS_CODE, DEFAULT_MESSAGE);
    }
}
