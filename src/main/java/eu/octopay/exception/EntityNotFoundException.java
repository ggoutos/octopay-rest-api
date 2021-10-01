package eu.octopay.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
        super("No data found");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

}
