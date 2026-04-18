package team.anonyms.converter.dto.controller.responses.errors;

public final class IllegalPatternExceptionErrorResponse extends ErrorResponse {
    private static final int STATUS_CODE = 400;
    private static final String DEFAULT_MESSAGE = "PATTERN";

    public IllegalPatternExceptionErrorResponse() {
        super(STATUS_CODE, DEFAULT_MESSAGE);
    }
}
