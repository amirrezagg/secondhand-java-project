package ir.aut.secondhand.exception;

public class UserIsNotAuthenticatedException extends RuntimeException {

    public UserIsNotAuthenticatedException() {
        super("User is not authenticated");
    }

    public UserIsNotAuthenticatedException(String message) {
        super(message);
    }

    public UserIsNotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
