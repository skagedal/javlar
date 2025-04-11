package skagedal.javlar.util;

public class BodyMapperException extends RuntimeException {
    public BodyMapperException(String message) {
        super(message);
    }

    public BodyMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public BodyMapperException(Throwable cause) {
        super(cause);
    }
}
