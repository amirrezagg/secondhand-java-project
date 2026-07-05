package ir.aut.secondhand.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ir.aut.secondhand.exception.BadCredentialsException;
import ir.aut.secondhand.exception.EmailAlreadyExistsException;
import ir.aut.secondhand.exception.PhoneNumberAlreadyExistsException;
import ir.aut.secondhand.exception.UsernameAlreadyExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static class ValidationErrorDetails {

        private final int status;
        private final String message;
        private final Map<String, String> errors;

        public ValidationErrorDetails(int status, String message, Map<String, String> errors) {
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

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ValidationErrorDetails> handleUsernameExists(UsernameAlreadyExistsException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("username", "This username is already taken");

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Registration Failed",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    public ResponseEntity<ValidationErrorDetails> handlePhoneNumberExists(PhoneNumberAlreadyExistsException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("phoneNumber", "This phone number is already registered");

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Registration Failed",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ValidationErrorDetails> handleEmailExists(EmailAlreadyExistsException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "This email address is already registered");

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Registration Failed",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ValidationErrorDetails> handleCredentials(BadCredentialsException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("credentials", "Invalid username or password");

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication Failed",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }
}
