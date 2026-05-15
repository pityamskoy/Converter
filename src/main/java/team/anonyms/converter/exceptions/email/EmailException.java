package team.anonyms.converter.exceptions.email;

@SuppressWarnings(value = {"unused"})
public abstract class EmailException extends RuntimeException {
    public EmailException() {
        super();
    }

    public EmailException(String message) {
        super(message);
    }

    public EmailException(Throwable cause) {
        super(cause);
    }

    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
