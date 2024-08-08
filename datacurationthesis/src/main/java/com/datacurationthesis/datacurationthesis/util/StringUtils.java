package com.datacurationthesis.datacurationthesis.util;

public class StringUtils {

    public static String capitalizeWords(String input) {
        String[] words = input.split("\\s+");
        StringBuilder capitalizeString = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                capitalizeString.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
            }
        }
        return capitalizeString.toString().trim();
    }
}
