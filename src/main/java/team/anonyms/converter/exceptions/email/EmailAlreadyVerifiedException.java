package team.anonyms.converter.exceptions.email;

public final class EmailAlreadyVerifiedException extends EmailException {
    public EmailAlreadyVerifiedException(String message) {
        super(message);
    }

    public EmailAlreadyVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}
