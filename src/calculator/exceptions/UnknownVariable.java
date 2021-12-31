package calculator.exceptions;

public class UnknownVariable extends RuntimeException {
    public UnknownVariable() {
        super();
    }

    public UnknownVariable(String errorMessage) {
        super(errorMessage);
    }

    public UnknownVariable(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
