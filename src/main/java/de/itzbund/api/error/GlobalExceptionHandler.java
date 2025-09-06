package de.itzbund.api.error;

import de.itzbund.service.exception.DuplicateIsbnException;
import de.itzbund.service.exception.VersionMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public final class GlobalExceptionHandler {

    /** Basismap für Error Response. */
    private Map<String, Object> base(final HttpStatus status, final String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }

    /** Validation Fehler. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(final MethodArgumentNotValidException ex) {
        Map<String, Object> body = base(HttpStatus.BAD_REQUEST, "Validation failed");
        body.put("fields", ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ":" + f.getDefaultMessage()).toList());
        return ResponseEntity.badRequest().body(body);
    }

    /** Duplicate ISBN Konflikt. */
    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<?> handleDuplicate(final DuplicateIsbnException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(base(HttpStatus.CONFLICT, ex.getMessage()));
    }

    /** Version Mismatch (Optimistic Lock). */
    @ExceptionHandler(VersionMismatchException.class)
    public ResponseEntity<?> handleVersion(final VersionMismatchException ex) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(base(HttpStatus.PRECONDITION_FAILED, ex.getMessage()));
    }

    /** Fallback Fehler. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOther(final Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(base(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }
}
