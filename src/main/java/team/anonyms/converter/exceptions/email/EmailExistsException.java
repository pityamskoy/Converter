package team.anonyms.converter.exceptions.email;

public final class EmailExistsException extends EmailException {
    public EmailExistsException(String message) {
        super(message);
    }

    public EmailExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
