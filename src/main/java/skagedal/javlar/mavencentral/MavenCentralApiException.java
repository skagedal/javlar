package skagedal.javlar.mavencentral;

public class MavenCentralApiException extends RuntimeException {
    public MavenCentralApiException(String message) {
        super(message);
    }

    public MavenCentralApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
