package ir.aut.secondhand.advice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ir.aut.secondhand.exception.DuplicateResourceException;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.exception.UserIsNotAuthenticatedException;

/**
 * Centralized exception handling component for REST controllers.
 *
 * <p>Captures common application exceptions and maps them to consistent
 * HTTP responses with structured payloads. Each handler translates
 * domain and framework exceptions into a small DTO describing the
 * error type, human-readable message, HTTP status code, timestamp,
 * and optionally a map of field-specific validation errors. This
 * ensures clients receive predictable error shapes for easier
 * client-side handling and debugging.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static class ValidationErrorDetails {

        private final String timestamp;
        private final int status;
        private final String message;
        private final Map<String, String> errors;

        /**
         * DTO ctor used to build a validation-oriented error response.
         *
         * @param status HTTP status code numeric value
         * @param message short summary of the validation failure
         * @param errors map of field names to human-readable error messages
         */
        public ValidationErrorDetails(int status, String message, Map<String, String> errors) {
            this.timestamp = Instant.now().toString();
            this.status = status;
            this.message = message;
            this.errors = errors;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, String> getErrors() {
            return errors;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    public static class AuthenticationErrorDetails {

        private final String timestamp;
        private final int status;
        private final String message;

        /**
         * DTO ctor used to build an authentication-related error response.
         *
         * @param status HTTP status code numeric value
         * @param message brief message describing the authentication failure
         */
        public AuthenticationErrorDetails(int status, String message) {
            this.timestamp = Instant.now().toString();
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Handle cases where the request body is either missing or cannot be parsed
     * (malformed JSON). Produces a BAD_REQUEST response with a short, stable
     * payload indicating the request body issue so clients can correct payload
     * formatting errors.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationErrorDetails> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();

        String genericMessage = "Required request body is missing or malformed";
        errors.put("requestBody", "The JSON request is unreadable or has invalid syntax");

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                genericMessage,
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    /**
     * Convert Spring's MethodArgumentNotValidException into a structured
     * validation error response that enumerates field-specific messages.
     * This allows clients to present precise validation feedback to users.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDetails> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error
                -> errors.put(error.getField(), error.getDefaultMessage())
        );

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    /**
     * Translate a domain-level ResourceNotFoundException into a BAD_REQUEST
     * style validation response. The exception is expected to expose the
     * relevant field name so the response can be bound to a specific input
     * or parameter that caused the not-found condition.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ValidationErrorDetails> handleResourceException(
            ResourceNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getField(), ex.getMessage());

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    /**
     * Handle attempts to create an already existing resource. Returns a
     * BAD_REQUEST payload that identifies the conflicting field and a
     * message suitable for client display during registration flows.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ValidationErrorDetails> handleResourceExists(DuplicateResourceException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getField(), ex.getMessage());

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Registration Failed",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    /**
     * Normalizes IllegalArgumentException messages into a client-friendly
     * validation error. The method performs lightweight message sanitization
     * to remove or replace internal tokens used by the server-side
     * validation logic before returning the message to the client.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ValidationErrorDetails> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();
        String errorMessage = ex.getMessage();
        // Replace internal tokens with a more user-facing prefix and remove
        // any remaining placeholder punctuation used by server validation.
        errorMessage = errorMessage.replace("$:", "Validation Error:").trim();
        errorMessage = errorMessage.replace("$.", "").trim();

        errors.put("global", errorMessage);

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request Argument",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    /**
     * Return a generic unauthorized response for failed credential checks
     * without exposing authentication internals. Keeps the message stable
     * to avoid leaking whether the username or password was incorrect.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthenticationErrorDetails> handleCredentials(BadCredentialsException ex) {
        AuthenticationErrorDetails errorDetails = new AuthenticationErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid username or password"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

    /**
     * Propagate explicit authentication-state failures (e.g. missing or
     * expired session/token) back to the client with the exception's
     * message so callers can decide how to recover (re-login, refresh, etc.).
     */
    @ExceptionHandler(UserIsNotAuthenticatedException.class)
    public ResponseEntity<AuthenticationErrorDetails> handleAuthentication(UserIsNotAuthenticatedException ex) {
        AuthenticationErrorDetails errorDetails = new AuthenticationErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

    /**
     * Handle low-level database integrity violations and present a safe,
     * non-technical message to clients. Avoids surfacing raw SQL or stack
     * traces while indicating a constraint problem occurred.
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ValidationErrorDetails> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("global", "A database constraint was violated. Please ensure all required fields are valid.");

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Database Integrity Error",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }
}
