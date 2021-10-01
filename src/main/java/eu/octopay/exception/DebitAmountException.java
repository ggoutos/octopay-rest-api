package eu.octopay.exception;

public class DebitAmountException extends RuntimeException {

    public DebitAmountException() {
        super("Το υπόλοιπο δεν επαρκεί για τη συναλλαγή.");
    }

    public DebitAmountException(String message) {
        super(message);
    }

}
