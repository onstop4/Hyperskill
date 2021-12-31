package calculator.exceptions;

public class BadAssignment extends RuntimeException {
    public BadAssignment() {
        super();
    }

    public BadAssignment(String errorMessage) {
        super(errorMessage);
    }

    public BadAssignment(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
