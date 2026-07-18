package ir.aut.secondhand.advice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import ir.aut.secondhand.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import ir.aut.secondhand.exception.DuplicateResourceException;
import ir.aut.secondhand.exception.UserIsNotAuthenticatedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static class ValidationErrorDetails {

        private final String timestamp;
        private final int status;
        private final String message;
        private final Map<String, String> errors;

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ValidationErrorDetails> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();
        String errorMessage = ex.getMessage();
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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthenticationErrorDetails> handleCredentials(BadCredentialsException ex) {
        AuthenticationErrorDetails errorDetails = new AuthenticationErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid username or password"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

    @ExceptionHandler(UserIsNotAuthenticatedException.class)
    public ResponseEntity<AuthenticationErrorDetails> handleAuthentication(UserIsNotAuthenticatedException ex) {
        AuthenticationErrorDetails errorDetails = new AuthenticationErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

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
