package team.anonyms.converter.exceptions;

public final class UnsupportedExtensionException extends IllegalArgumentException {
    public UnsupportedExtensionException(String message) {
        super(message);
    }

    public UnsupportedExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
