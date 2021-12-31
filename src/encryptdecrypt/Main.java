package encryptdecrypt;

import java.io.*;
import java.util.Scanner;

class Arguments {
    String mode;
    String inputFromCommand;
    String inputFilename;
    String outputFilename;
    String key;
    String algorithm;

    private static String getArgValue(String[] args, String argumentName) {
        for (int i = 0; i < args.length; i += 2) {
            if (argumentName.equals(args[i])) {
                try {
                    return args[i + 1];
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }
            }
        }
        return null;
    }

    static Arguments parseArgs(String[] args) {
        Arguments parsedArgs = new Arguments();
        parsedArgs.mode = getArgValue(args, "-mode");
        parsedArgs.inputFromCommand = getArgValue(args, "-data");
        parsedArgs.inputFilename = getArgValue(args, "-in");
        parsedArgs.outputFilename = getArgValue(args, "-out");
        parsedArgs.key = getArgValue(args, "-key");
        parsedArgs.algorithm = getArgValue(args, "-alg");

        return parsedArgs;
    }
}

abstract class ShiftAlgo {
    abstract char shiftChar(char c, int shift);

    String shiftMultipleChars(String original, int shift) {
        char[] characters = original.toCharArray();

        for (int i = 0; i < characters.length; i++) {
            char newCharacter = shiftChar(characters[i], shift);
            characters[i] = newCharacter;
        }

        return String.valueOf(characters);
    }

    String shiftMultipleLines(String original, int shift) {
        Scanner scanner = new Scanner(original);
        StringBuilder sb = new StringBuilder();

        if (scanner.hasNextLine()) {
            while (true) {
                String originalLine = scanner.nextLine();
                String shiftedLine = shiftMultipleChars(originalLine, shift);
                sb.append(shiftedLine);
                if (!scanner.hasNextLine()) {
                    break;
                }
                sb.append(System.lineSeparator());
            }
        }

        scanner.close();
        return sb.toString();
    }
}

class UnicodeShift extends ShiftAlgo {
    @Override
    char shiftChar(char c, int shift) {
        return (char) (c + shift);
    }
}

class LetterShift extends ShiftAlgo {
    private char shiftLetter(char c, int shift) {
        int letter = c - ('a' - 1);
        letter += shift;
        if (letter < 0) {
            letter += 26;
        }
        letter %= 26;
        letter += 'a' - 1;
        return (char) letter;
    }

    @Override
    char shiftChar(char c, int shift) {
        char newChar = c;
        if (Character.isLetter(c)) {
            newChar = shiftLetter(c, shift);
        }
        return newChar;
    }
}

class TextShifter {
    ShiftAlgo algo;

    TextShifter(ShiftAlgo algorithm) {
        this.algo = algorithm;
    }

    TextShifter(String algo) {
        if ("unicode".equalsIgnoreCase(algo)) {
            this.algo = new UnicodeShift();
        } else {
            this.algo = new LetterShift();
        }
    }

    String encrypt(String original, int key) {
        return algo.shiftMultipleLines(original, key);
    }

    String decrypt(String original, int key) {
        return algo.shiftMultipleLines(original, -key);
    }
}

abstract class Input {
    abstract String getInputText();
}

class InputFromCommand extends Input {
    String dataArg;

    String getInputText() {
        return dataArg;
    }

    InputFromCommand(String text) {
        dataArg = text;
    }
}

class InputFromFile extends Input {
    String filename;

    String getInputText() {
        try (Scanner input = new Scanner(new File(filename))) {
            StringBuilder sb = new StringBuilder();
            while (input.hasNextLine()) {
                sb.append(input.nextLine());
            }
            return String.valueOf(sb);
        } catch (IOException e) {
            return "";
        }
    }

    InputFromFile(String filename) {
        this.filename = filename;
    }
}

abstract class Output {
    abstract void outputText(String text);
}

class StdOutput extends Output {
    void outputText(String text) {
        System.out.println(text);
    }
}

class OutputToFile extends Output {
    String filename;

    OutputToFile(String filename) {
        this.filename = filename;
    }

    void outputText(String text) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(text);
        } catch (IOException e) {}
    }
}

public class Main {
    private static boolean isNotNull(String value) {
        return value != null;
    }

    public static void main(String[] args) {
        Arguments parsedArgs = Arguments.parseArgs(args);

        Input inputMethod = null;
        if (isNotNull(parsedArgs.inputFilename)) {
            inputMethod = new InputFromFile(parsedArgs.inputFilename);
        }
        if (isNotNull(parsedArgs.inputFromCommand)) {
            inputMethod = new InputFromCommand(parsedArgs.inputFromCommand);
        }
        String text = inputMethod.getInputText();

        TextShifter shifter = new TextShifter(parsedArgs.algorithm);

        String result = text;

        int key = 0;
        if (isNotNull(parsedArgs.key)) {
            key = Integer.parseInt(parsedArgs.key);
        }

        if ("enc".equalsIgnoreCase(parsedArgs.mode)) {
            result = shifter.encrypt(text, key);
        } else if ("dec".equalsIgnoreCase(parsedArgs.mode)) {
            result = shifter.decrypt(text, key);
        }

        Output outputMethod;
        if (isNotNull(parsedArgs.outputFilename)) {
            outputMethod = new OutputToFile(parsedArgs.outputFilename);
        } else {
            outputMethod = new StdOutput();
        }

        outputMethod.outputText(result);
    }
}
