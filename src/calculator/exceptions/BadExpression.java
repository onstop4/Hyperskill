package calculator.exceptions;

public class BadExpression extends RuntimeException {
    public BadExpression() {
        super();
    }

    public BadExpression(String errorMessage) {
        super(errorMessage);
    }

    public BadExpression(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
