package ir.aut.secondhand.exception;

public class PhoneNumberAlreadyExistsException extends RuntimeException {

    public PhoneNumberAlreadyExistsException() {
        super("This phone number already exists");
    }

    public PhoneNumberAlreadyExistsException(String message) {
        super(message);
    }

    public PhoneNumberAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
