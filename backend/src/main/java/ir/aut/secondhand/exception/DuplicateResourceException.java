package ir.aut.secondhand.exception;

public class DuplicateResourceException extends RuntimeException {

    private final String field;

    public DuplicateResourceException(String field) {
        super(field.substring(0, 1).toUpperCase() + field.substring(1) + " is Already Exists");
        this.field = field;
    }

    public DuplicateResourceException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
