package calculator.exceptions;

public class TooLarge extends RuntimeException {
    public TooLarge() {
        super();
    }

    public TooLarge(String errorMessage) {
        super(errorMessage);
    }

    public TooLarge(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
