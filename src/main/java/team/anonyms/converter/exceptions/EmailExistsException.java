package team.anonyms.converter.exceptions;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException() {
        super();
    }

    public EmailExistsException(String message) {
        super(message);
    }

    public EmailExistsException(Throwable cause) {
        super(cause);
    }

    public EmailExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
