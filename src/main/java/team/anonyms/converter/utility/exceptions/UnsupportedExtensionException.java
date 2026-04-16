package team.anonyms.converter.utility.exceptions;

public class UnsupportedExtensionException extends RuntimeException {
    public UnsupportedExtensionException() {
        super();
    }

    public UnsupportedExtensionException(String message) {
        super(message);
    }

    public UnsupportedExtensionException(Throwable cause) {
        super(cause);
    }

    public UnsupportedExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
