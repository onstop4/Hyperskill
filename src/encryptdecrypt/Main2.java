package encryptdecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main2 {

    private static char shiftChar(char c, int shift) {
        return (char) (c + shift);
    }

    private static String shiftMultipleChars(String original, int shift) {
        char[] characters = original.toCharArray();

        for (int i = 0; i < characters.length; i++) {
            char newCharacter = shiftChar(characters[i], shift);
            characters[i] = newCharacter;
        }

        return String.valueOf(characters);
    }

    private static String encrypt(String original, int key) {
        return shiftMultipleChars(original, key);
    }

    private static String decrypt(String original, int key) {
        return shiftMultipleChars(original, -key);
    }

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

    private static boolean isInvalidArg(String arg) {
        return arg == null;
    }

    private static String getDataFromFile(String filename) throws IOException {
        try (Scanner input = new Scanner(new File(filename))) {
            StringBuilder sb = new StringBuilder();
            while (input.hasNextLine()) {
                sb.append(input.nextLine());
            }
            return String.valueOf(sb);
        }
    }

    private static void writeDataToFile(String filename, String data) throws IOException {
        FileWriter file = new FileWriter(filename);
        file.write(data);
        file.close();
    }

    public static void main(String[] args) {

        String inputFilename = getArgValue(args, "-in");
        String outputFilename = getArgValue(args, "-out");

        String mode = getArgValue(args, "-mode");
        if (isInvalidArg(mode)) {
            mode = "enc";
        }
        String key = getArgValue(args, "-key");
        if (isInvalidArg(key)) {
            key = "0";
        }
        String dataFromCommand = getArgValue(args, "-data");

        try {
            int keyInt = Integer.parseInt(key);
            String result = "";
            String data = "";

            if (inputFilename != null) {
                data = getDataFromFile(inputFilename);
            }
            if (dataFromCommand != null) {
                data = dataFromCommand;
            }

            if (mode.equalsIgnoreCase("enc")) {
                result = encrypt(data, keyInt);
            } else if (mode.equalsIgnoreCase("dec")) {
                result = decrypt(data, keyInt);
            }

            if (outputFilename != null) {
                writeDataToFile(outputFilename, result);
            } else {
                System.out.println(result);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find input file.");
        } catch (IOException e) {
            System.out.println("IO error. Check file permissions.");
        }
    }
}
