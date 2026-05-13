package team.anonyms.converter.exceptions.email;

public final class EmailAlreadyExistsException extends EmailException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
