package skagedal.javlar.domain;

public class NoSuchLibraryException extends RuntimeException{
    public NoSuchLibraryException(String message) {
        super(message);
    }
}
