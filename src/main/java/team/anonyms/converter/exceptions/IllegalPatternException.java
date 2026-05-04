package team.anonyms.converter.exceptions;

public class IllegalPatternException extends IllegalArgumentException {
    public IllegalPatternException() {
        super();
    }

    public IllegalPatternException(String message) {
        super(message);
    }

    public IllegalPatternException(Throwable cause) {
        super(cause);
    }

    public IllegalPatternException(String message, Throwable cause) {
        super(message, cause);
    }
}
