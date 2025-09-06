package de.itzbund.error;

public class DuplicateIsbnException extends RuntimeException {
    public DuplicateIsbnException(final String isbn) {
        super("ISBN bereits vergeben: " + isbn);
    }
}
