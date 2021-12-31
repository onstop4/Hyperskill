package calculator.exceptions;

public class BadIdentifier extends RuntimeException {
    public BadIdentifier() {
        super();
    }

    public BadIdentifier(String errorMessage) {
        super(errorMessage);
    }

    public BadIdentifier(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
