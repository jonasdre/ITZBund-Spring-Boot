package de.itzbund.service.exception;

public class VersionMismatchException extends RuntimeException {
    public VersionMismatchException(final Long id, final Long expected, final Long actual) {
        super("Versionskonflikt für ID=" + id + " erwartet=" + expected + " aktuell=" + actual);
    }
}
