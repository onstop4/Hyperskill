package correcter;

import java.util.Scanner;
import java.util.Random;

public class Main {
    private final static String allValidReplacements = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static int randRange(Random rnd, int lower, int upper) {
        return rnd.nextInt(upper - lower) + lower;
    }

    private static void alterOneChar(Random rnd, StringBuilder sb, int start, int end) {
        int indexToChange = randRange(rnd, start, end);
        int indexOfNewChar = randRange(rnd, 0, allValidReplacements.length());
        char newChar = allValidReplacements.charAt(indexOfNewChar);
        sb.setCharAt(indexToChange, newChar);
    }

    private static String makeErrors(Random rnd, String original) {
        StringBuilder sb = new StringBuilder(original);

        for (int start = 0; start < sb.length(); start += 3) {
            int end = start + 3;
            if (end <= original.length()) {
                String originalSubstring = sb.substring(start, end);
                String newSubstring = originalSubstring;
                while (originalSubstring.equals(newSubstring)) {
                    alterOneChar(rnd, sb, start, end);
                    newSubstring = sb.substring(start, end);
                }
            }
        }

        return sb.toString();
    }

    private static String tripleEachChar(String original) {
        StringBuilder sb = new StringBuilder(original);

        for (int iOriginal = 0, iSB = 0; iOriginal < original.length(); iOriginal++, iSB += 3) {
            char current = original.charAt(iOriginal);
            for (int j = 1; j < 3; j++) {
                sb.insert(iSB, current);
            }
        }

        return sb.toString();
    }

    private static char restoreChar(String original) {
        char c1 = original.charAt(0);
        char c2 = original.charAt(1);
        if (original.charAt(2) == c1) {
            return c1;
        }
        return c2;
    }

    private static String restoreMessage(String original) {
        char[] chars = new char[original.length() / 3];

        for (int i = 0; i < chars.length; i++) {
            String substring = original.substring(i * 3, i * 3 + 3);
            char restoredChar = restoreChar(substring);
            chars[i] = restoredChar;
        }

        return String.valueOf(chars);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        String text = scanner.nextLine();
        System.out.println(text);
        text = tripleEachChar(text);
        System.out.println(text);
        text = makeErrors(random, text);
        System.out.println(text);
        text = restoreMessage(text);
        System.out.println(text);

        scanner.close();
    }
}
