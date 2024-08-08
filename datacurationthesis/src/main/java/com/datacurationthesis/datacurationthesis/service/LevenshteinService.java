package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class LevenshteinService {

    private List<String> dictionaryWords;

    public LevenshteinService() {
       try {
           LoggerController.info("Loading dictionary...");
           loadDictionary("dictionaries/el_GR.dic");
           LoggerController.info("Dictionary loaded.");
       } catch (Exception e) {
           LoggerController.formattedError("Error loading dictionary words: %s", e.getMessage());
       }
    }

    // Method to load dictionary words
    private void loadDictionary(String dicFilePath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(dicFilePath);
        if (inputStream == null) {
            LoggerController.formattedError("Dictionary file not found: %s", dicFilePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
               String line = reader.readLine();
               if (line != null) {
                   // First line usually contains the number of words, ignore it
                   line = reader.readLine();
               }
               dictionaryWords = new ArrayList<>();
            while (line != null) {

                dictionaryWords.add(line.trim());
                line = reader.readLine();
            }
        } catch (IOException e) {
            LoggerController.formattedError("Error loading dictionary words: %s", e.getMessage());
        }
    }

    // Method to find the closest matching word in the dictionary
    public String suggestClosestWord(String word) {
        String closestWord = null;
        int minDistance = Integer.MAX_VALUE;

        for (String dictWord : dictionaryWords) {
            int distance = calculateLevenshteinDistance(word.toLowerCase(), dictWord);
            if (distance < minDistance) {
                minDistance = distance;
                closestWord = dictWord;
            }
        }
        LoggerController.formattedInfo("Closest word for '%s' is '%s'", word, closestWord);
        return closestWord;
    }

    // Levenshtein distance algorithm
    private int calculateLevenshteinDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                            dp[i - 1][j - 1] + costOfSubstitution(word1.charAt(i - 1), word2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                    );
                }
            }
        }
        return dp[word1.length()][word2.length()];
    }

    private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private int min(int... numbers) {
        int minValue = Integer.MAX_VALUE;
        for (int number : numbers) {
            if (number < minValue) {
                minValue = number;
            }
        }
        return minValue;
    }

    // Hamming distance algorithm
    public int calculateHammingDistance(String word1, String word2) {
        if (word1.length() != word2.length()) {
           throw new IllegalArgumentException("Word lengths must be equal");
        }
        int distance = 0;
        for (int i = 0; i < word1.length(); i++) {
            if (word1.charAt(i) != word2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }

    // Method to find the closest matching word in the dictionary using Hamming distance
    public String hammingDistanceCalculate(String word) {
        String closestWord = null;
        int minDistance = Integer.MAX_VALUE;

        for (String dictWord : dictionaryWords) {
            if (dictWord.length() == word.length()) {
                int distance = calculateHammingDistance(word, dictWord);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestWord = dictWord;
                }
            }
        }
        return closestWord;
    }

    // Method to handle sentences with multiple words
    public String hammingDistanceForSentence(String sentence) {
        String[] words = sentence.split("\\s+");
        StringBuilder correctedSentence = new StringBuilder();

        for (String word : words) {
            String correctedWord = hammingDistanceCalculate(word);
            if (correctedWord != null) {
                correctedSentence.append(correctedWord).append(" ");
            } else {
                correctedSentence.append(word).append(" "); // No correction available, keep original
            }
        }
        return correctedSentence.toString().trim();
    }

    // Test the suggestion method
    public static void testLevenshtein() {
        LevenshteinService service = new LevenshteinService();
        String[] words = {"καλημέρα", "καλημρα", "καλοκαίρι", "καλογηρία", "ΒίΔα"};
        String suggestion;
        for (String w : words) {
            suggestion = service.suggestClosestWord(w);
            LoggerController.formattedInfo("Did you mean for word: %s", w + " -> " + suggestion);
        }
    }

}
