package ir.aut.secondhand.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String field;

    public ResourceNotFoundException(String field) {
        super(field + " not found");
        this.field = field;
    }

    public ResourceNotFoundException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
