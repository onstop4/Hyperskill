package calculator.calc;

import calculator.exceptions.BadAssignment;
import calculator.exceptions.BadExpression;
import calculator.exceptions.BadIdentifier;
import calculator.exceptions.UnknownVariable;

import java.math.BigInteger;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathProcess {
    private static final Pattern assignmentDetection = Pattern.compile("(.+)=(.*)");
    private static final Pattern evenDashesDetection = Pattern.compile("(?:--|\\+)+");
    private static final Pattern oddDashesDetection = Pattern.compile("-(?:--)+");
    private static final Pattern validVariableDetection = Pattern.compile("[A-Za-z]+");
    private static final Pattern validMathDetection = Pattern.compile("(?:[+\\-*/()^]|\\d)+");
    private static final Pattern invalidOperatorsDetection = Pattern.compile("[+\\-*/()^]{2,}");
    private static final Pattern invalidExpressionDetection = Pattern.compile("\\d[A-Za-z]|[A-Za-z]\\d");
    private static final Pattern parenthesesMultiplyDetection1 = Pattern.compile("([^+\\-*/(^])\\(");
    private static final Pattern parenthesesMultiplyDetection2 = Pattern.compile("\\)([^+\\-*/)^])");
    private static final Pattern negativeParenthesesDetection = Pattern.compile("-\\(");
    private static final Pattern anyNumbersDetection = Pattern.compile(".*-?\\d+.*");
    private static final Pattern anyInvalidChars = Pattern.compile("[^A-Za-z\\d+\\-*/()^]");

    public static Answer solve(String input, Map<String, BigInteger> variables) {
        String current = input.replaceAll("\\s+", "");
        Matcher potentialAssignment = assignmentDetection.matcher(current);
        if (potentialAssignment.matches()) {
            String variable = potentialAssignment.group(1);
            String expression = potentialAssignment.group(2);
            if (!validVariableDetection.matcher(variable).matches()) {
                throw new BadIdentifier();
            }
            if (variable.contains("=") || expression.contains("=")) {
                throw new BadAssignment();
            }

            try {
                BigInteger result = solveExpression(expression, variables);
                variables.put(variable, result);
                return new Answer();
            } catch (BadExpression e) {
                throw new BadAssignment();
            }
        }

        return new Answer(solveExpression(current, variables));
    }

    private static BigInteger solveExpression(String input, Map<String, BigInteger> variables) {
        String current = input;
        current = substituteVariables(current, variables);
        if (!anyNumbersDetection.matcher(current).matches()) {
            throw new BadExpression();
        }
        current = handleBasicExpression(current);
        return ExpressionSolver.solve(current);
    }

    private static String handleBasicExpression(String input) {
        String current = input;
        current = handleDashes(current);
        current = handleParentheses(current);
        if (invalidOperatorsDetection.matcher(current).matches()) {
            throw new BadExpression();
        }
        return current;
    }

    private static String substituteVariables(String original, Map<String, BigInteger> variables) {
        if (invalidExpressionDetection.matcher(original).matches() || anyInvalidChars.matcher(original).matches()) {
            throw new BadAssignment();
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = validVariableDetection.matcher(original);

        while (matcher.find()) {
            String variable = matcher.group();
            BigInteger value = variables.getOrDefault(variable, null);
            if (value == null) {
                throw new UnknownVariable();
            }
            matcher.appendReplacement(sb, String.valueOf(value));
        }

        matcher.appendTail(sb);
        String result = sb.toString();
        if (!validMathDetection.matcher(result).matches()) {
            throw new BadAssignment();
        }
        return result;
    }

    private static String handleDashes(String original) {
        String current = original;
        current = oddDashesDetection.matcher(current).replaceAll("-");
        current = evenDashesDetection.matcher(current).replaceAll("+");
        current = current.replaceAll("-\\+", "+-");
        current = current.replaceAll("(?<!\\+)-(?![-(])", "+-");
        current = current.replaceAll("(?<=[*/^])\\+", "");
        if (current.startsWith("+")) {
            current = current.substring(1);
        }
        return current;
    }

    private static int countChar(String input, char c) {
        return input.length() - input.replace(String.valueOf(c), "").length();
    }

    private static String handleParentheses(String original) {
        String current = original;

        if (original.contains("(") || original.contains(")")) {
            current = negativeParenthesesDetection.matcher(current).replaceAll("+-1*(");
            current = parenthesesMultiplyDetection1.matcher(current).replaceAll("*");
            current = parenthesesMultiplyDetection2.matcher(current).replaceAll("*");

            if (countChar(original, '(') != countChar(original, ')')) {
                throw new BadExpression();
            }

//            Matcher parenthesesDetected = parenthesisExpressionDetection.matcher(current);
//            while (parenthesesDetected.matches()) {
//                String expression = parenthesesDetected.group(1);
//                int result = solve(expression);
//                current = parenthesesDetected.replaceFirst(String.valueOf(result));
//                parenthesesDetected = parenthesisExpressionDetection.matcher(current);
//            }
        }

        return current;
    }
}
