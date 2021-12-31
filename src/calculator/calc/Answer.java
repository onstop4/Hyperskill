package calculator.calc;

import java.math.BigInteger;

public class Answer {
    final public boolean show;
    final public BigInteger value;

    Answer() {
        show = false;
        value = BigInteger.ZERO;
    }

    Answer(BigInteger value) {
        show = true;
        this.value = value;
    }
}
