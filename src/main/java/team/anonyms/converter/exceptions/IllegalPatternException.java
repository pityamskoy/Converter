package team.anonyms.converter.exceptions;

public final class IllegalPatternException extends IllegalArgumentException {
    public IllegalPatternException(String message) {
        super(message);
    }

    public IllegalPatternException(String message, Throwable cause) {
        super(message, cause);
    }
}
