package ir.aut.secondhand.exception;

public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException() {
        super("Incorrect username or password");
    }

    public BadCredentialsException(String message) {
        super(message);
    }

    public BadCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

}
