package team.anonyms.converter.dto.controller.responses.errors;

public final class IllegalPatternExceptionErrorResponse extends ErrorResponse {
    private final static int STATUS_CODE = 400;
    private final static String DEFAULT_MESSAGE = "PATTERN";

    public IllegalPatternExceptionErrorResponse() {
        super(STATUS_CODE, DEFAULT_MESSAGE);
    }
}
