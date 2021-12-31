package calculator.calc;

import calculator.exceptions.BadExpression;
import calculator.exceptions.TooLarge;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ExpressionSolver {
    private static final Pattern numbersDetection = Pattern.compile("-?\\d+");
    private static final Pattern operatorsDetection = Pattern.compile("[+*/^()]");
    private static final Pattern mathDetection = Pattern.compile("-?\\d+|[+*/^()]");

    private static final List<Character> precedence = List.of('(', '+', '*', '/', '^');

    static BigInteger solve(String input) {
        Deque<String> items = new ArrayDeque<>();

        try {
            add(input, items);
            return calc(items);
        } catch (NoSuchElementException e) {
            throw new BadExpression();
        }
    }

    private static List<String> split(String input) {
        List<String> list = new ArrayList<>();
        Matcher matcher = mathDetection.matcher(input);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    private static int compare(String existing, String incoming) {
//        if ("(".equals(incoming)) {
//            return -1;
//        }
        int existingPosition = precedence.indexOf(existing.charAt(0));
        int incomingPosition = precedence.indexOf(incoming.charAt(0));
        return incomingPosition - existingPosition;
    }

    private static void popUntil(String stop, Deque<String> items, Deque<String> operators) {
        String existingOperator = operators.peekLast();
        while (existingOperator != null && !("(".equals(existingOperator)) && compare(stop, existingOperator) >= 0) {
            items.addLast(operators.removeLast());
            existingOperator = operators.peekLast();
        }
        if ("(".equals(existingOperator)) {
            operators.removeLast();
        }
    }

    private static void add(String input, Deque<String> items) {
        Deque<String> operators = new ArrayDeque<>();
        List<String> tokens = split(input);

        for (String current : tokens) {
            if (numbersDetection.matcher(current).matches()) {
                items.addLast(current);
            } else if (operatorsDetection.matcher(current).matches()) {
                String existingOperator = operators.peekLast();
                if (existingOperator == null || "(".equals(existingOperator)) {
                    operators.addLast(current);
                } else if (")".equals(current)) {
                    popUntil("(", items, operators);
//                    if ("(".equals(operators.peekLast())) {
//                        operators.removeLast();
//                    } else {
//                        throw new RuntimeException("Potentially incorrect behavior for parentheses.");
//                    }
                } else if ("(".equals(current) || compare(existingOperator, current) > 0) {
                    operators.addLast(current);
                } else {
                    popUntil(current, items, operators);
                    operators.addLast(current);
                }
            }
            else {
                throw new RuntimeException("Invalid input in addToStack().");
            }
        }

        while (!operators.isEmpty()) {
            items.addLast(operators.removeLast());
        }
    }

    private static BigInteger calc(Deque<String> items) {
        Deque<BigInteger> results = new ArrayDeque<>();

        while (!items.isEmpty()) {
            String current = items.removeFirst();
            if (numbersDetection.matcher(current).matches()) {
                results.addLast(new BigInteger(current));
            } else {
                BigInteger b = results.removeLast();
                BigInteger a = results.removeLast();

                BigInteger singleResult = performOp(a, b, current);
                results.addLast(singleResult);
            }
        }

        if (results.isEmpty()) {
            return BigInteger.ZERO;
        }
        return results.peekLast();
    }

    private static BigInteger performOp(BigInteger a, BigInteger b, String op) {
        BigInteger result;
        if ("+".equals(op)) {
            result = a.add(b);
        } else if ("*".equals(op)) {
            result = a.multiply(b);
        } else if ("/".equals(op)) {
            result = a.divide(b);
        } else if ("^".equals(op)) {
            try {
                result = a.pow(Math.toIntExact(b.longValueExact()));
            } catch (ArithmeticException e) {
                throw new TooLarge();
            }
        } else {
            throw new RuntimeException("Unknown operator \"" + op + "\" in performOp.");
        }
        return result;
    }
}
