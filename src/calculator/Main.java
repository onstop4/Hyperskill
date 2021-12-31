package calculator;

import calculator.calc.*;
import calculator.commands.CommandRunner;
import calculator.exceptions.BadAssignment;
import calculator.exceptions.BadExpression;
import calculator.exceptions.BadIdentifier;
import calculator.exceptions.UnknownVariable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void runCalculator(Scanner scanner, boolean showPrompt) {
        Map<String, BigInteger> variables = new HashMap<>();
        while (true) {
            if (showPrompt) {
                System.out.print(">>> ");
            }
            String line = scanner.nextLine().replaceAll("\\s+", "");

            if (line.isBlank()) {
                continue;
            }

            try {
                var commandType = CommandRunner.getCommandType(line);
                if (commandType != null) {
                    CommandRunner command = new CommandRunner(commandType);
                    if (command.run()) {
                        break;
                    }
                    continue;
                }
            } catch (BadExpression e) {
                System.out.println("Unknown command");
            }

            try {
                Answer answer = MathProcess.solve(line, variables);
                if (answer.show) {
                    System.out.println(answer.value);
                }
            } catch (BadExpression e) {
                System.out.println("Invalid expression");
            } catch (ArithmeticException e) {
                System.out.println("Divide by zero error");
            } catch (BadAssignment e) {
                System.out.println("Invalid assignment");
            } catch (BadIdentifier e) {
                System.out.println("Invalid identifier");
            } catch (UnknownVariable e) {
                System.out.println("Unknown variable");
            }
        }

        System.out.println("Bye!");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean showPrompt = false;
        for (String arg : args) {
            if ("--showPrompt".equals(arg)) {
                showPrompt = true;
                break;
            }
        }

        runCalculator(scanner, showPrompt);

        scanner.close();
    }
}
