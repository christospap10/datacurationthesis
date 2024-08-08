package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.dto.SpellCheckResponse;
import com.datacurationthesis.datacurationthesis.dto.SpellCheckResponseBasic;
import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class GreekSpellCkeckerService {



    public interface Hunspell extends Library {
        Hunspell INSTANCE = (Hunspell) Native.load("hunspell", Hunspell.class);
        Pointer Hunspell_create(String affpath, String dpath);
        void Hunspell_destroy(Pointer hunspell);
        int Hunspell_spell(Pointer hunspell, String word);
        Pointer Hunspell_suggest(Pointer hunspell, String word);
        void Hunspell_free_list(Pointer hunspell, Pointer[] list, int length);
    }

    private Pointer hunspell;

    @Value("${spellchecker.affFilePath}")
    private Resource affFileResource;

    @Value("${spellchecker.dicFilePath}")
    private Resource dicFileResource;

    private final LevenshteinService levenshteinService;
   @Autowired
    public GreekSpellCkeckerService(LevenshteinService levenshteinService) {
        this.levenshteinService = levenshteinService;
    }

    @PostConstruct
    public void init() throws IOException {
        try {
            // Get absolute paths from resources
            File affFile = affFileResource.getFile();
            File dicFile = dicFileResource.getFile();

            if (!affFile.exists() || !dicFile.exists()) {
                LoggerController.formattedError("Dictionary file(s) not found.");
            }

            // Debug: Print file paths
            LoggerController.formattedInfo("AFF File Path: %s", affFile.getAbsolutePath());
            LoggerController.formattedInfo("DIC File Path: %s", dicFile.getAbsolutePath());
            // Initialize Hunspell with dictionary files
            hunspell = Hunspell.INSTANCE.Hunspell_create(affFile.getAbsolutePath(), dicFile.getAbsolutePath());
            if (hunspell == null) {
                LoggerController.formattedError("Hunspell initialization failed");
                throw new IllegalStateException("Failed to initialize Hunspell with the given files.");
            }
        } catch (IOException e) {
            LoggerController.formattedError("Error loading dictionary files: %s", e.getMessage());
        } catch (Exception e) {
            LoggerController.formattedError("Unexpected error during initialization: %s", e.getMessage());
        }

    }

    public boolean isCorrect(String word) {
        return Hunspell.INSTANCE.Hunspell_spell(hunspell, word) == 1;
    }

    public List<String> getSuggestions(String word) {
        LoggerController.formattedInfo("Getting suggestions for: %s", word);
        Pointer suggestionsPointer = Hunspell.INSTANCE.Hunspell_suggest(hunspell, word);
        if (suggestionsPointer == null) {
            LoggerController.formattedInfo("No suggestions found for: %s", word);
            return new ArrayList<>();
        }

        List<String> suggestions = new ArrayList<>();
        Pointer[] suggestionArray = suggestionsPointer.getPointerArray(0);
        for (Pointer suggestion : suggestionArray) {
            String suggestionString = suggestion.getString(0,"UTF-8");
            LoggerController.formattedInfo("Suggestion: %s", suggestionString);
            if (suggestionString != null) {
                suggestions.add(suggestionString);
            }
        }
        Hunspell.INSTANCE.Hunspell_free_list(hunspell, suggestionArray, suggestionArray.length);
        return suggestions;
    }

    @PreDestroy
    public void close() {
        if (hunspell != null) {
            Hunspell.INSTANCE.Hunspell_destroy(hunspell);
        }
    }

    // Method to check if a word is correct and suggest alternatives if not
    public SpellCheckResponse checkAndSuggestWord(String word) {
        boolean correct = isCorrect(word);
        LoggerController.formattedInfo("Is the word '%s' correct? %s", word, correct);
       List<String>  hunspellSuggestions = getSuggestions(word);
       String levenshteinSuggestion = null;
       if (!correct && hunspellSuggestions.isEmpty()) {
           levenshteinSuggestion = levenshteinService.suggestClosestWord(word);
           LoggerController.formattedInfo("No hunspell suggestions found.\n Levenshtein suggestion: %s", levenshteinSuggestion);
       }
       return new SpellCheckResponse(word, correct, hunspellSuggestions, levenshteinSuggestion);
       }



    // New method to handle multi-word strings
    public SpellCheckResponse checkAndSuggestSentence(String sentence) {
        String[] words = sentence.split("\\s+");  // Split by whitespace
        StringBuilder correctedSentence = new StringBuilder();
        boolean allCorrect = true;
        List<String> allSuggestions = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        for (String word : words) {
            SpellCheckResponse response = checkAndSuggestWord(word);
            if (!response.isCorrect()) {
                allCorrect = false;
                String bestSuggestion = response.getLevenshteinSuggestion();

                if (bestSuggestion != null) {
                    System.out.printf("Original word: %s\n", word);
                    System.out.printf("Levenshtein suggestion: %s\n", bestSuggestion);
                    System.out.println("Suggestions:");
                    for (int i = 0; i < response.getHunspellSuggestions().size(); i++) {
                        System.out.printf("%d. %s\n", i + 1, response.getHunspellSuggestions().get(i));
                    }
                    System.out.print("Enter the number of the suggestion you want to apply (or press enter to keep original): ");
                    String input = scanner.nextLine().trim();

                    if (!input.isEmpty()) {
                        try {
                            int choice = Integer.parseInt(input) - 1;
                            if (choice >= 0 && choice < response.getHunspellSuggestions().size()) {
                                bestSuggestion = response.getHunspellSuggestions().get(choice);
                            } else {
                                System.out.print("Enter your custom correction (or press enter to keep original): ");
                                String customCorrection = scanner.nextLine().trim();
                                if (!customCorrection.isEmpty()) {
                                    bestSuggestion = customCorrection;
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.print("Enter your custom correction (or press enter to keep original): ");
                            String customCorrection = scanner.nextLine().trim();
                            if (!customCorrection.isEmpty()) {
                                bestSuggestion = customCorrection;
                            }
                        }
                    } else {
                        bestSuggestion = word; // Keep the original word
                    }
                    correctedSentence.append(bestSuggestion).append(" ");
                } else {
                    correctedSentence.append(word).append(" "); // No correction available, keep original
                }
            } else {
                correctedSentence.append(word).append(" ");
            }

            allSuggestions.addAll(response.getHunspellSuggestions());
            if (response.getLevenshteinSuggestion() != null) {
                allSuggestions.add(response.getLevenshteinSuggestion());
            }
        }

        return new SpellCheckResponse(sentence, allCorrect, allSuggestions, correctedSentence.toString().trim());
    }




    public SpellCheckResponseBasic checkAndSuggestSentenceBasic(String sentence) {
        String[] words = sentence.split("\\s+");  // Split by whitespace
        StringBuilder correctedSentence = new StringBuilder();
        boolean allCorrect = true;
        List<String> allSuggestions = new ArrayList<>();

        for (String word : words) {
            SpellCheckResponse response = checkAndSuggestWord(word);
            if (!response.isCorrect()) {
                allCorrect = false;
                String bestSuggestion = response.getHunspellSuggestions().isEmpty()
                        ? response.getLevenshteinSuggestion()
                        : response.getHunspellSuggestions().get(0);

                if (bestSuggestion != null) {
                    correctedSentence.append(bestSuggestion).append(" ");
                } else {
                    correctedSentence.append(word).append(" "); // No correction available, keep original
                }
            } else {
                correctedSentence.append(word).append(" ");
            }

            allSuggestions.addAll(response.getHunspellSuggestions());
            if (response.getLevenshteinSuggestion() != null) {
                allSuggestions.add(response.getLevenshteinSuggestion());
            }
        }

        return new SpellCheckResponseBasic(sentence, allSuggestions);
    }

    // A method to demonstrate the spell checker functionality
    public SpellCheckResponse testGreekSpellCheck() {
        String[] words = {"καλημέρα", "καλημρα", "καλοκαίρι", "καλογηρία", "απόγμα"};
        String sentence = "Γει σου Γιργο Καλημρα";
        LoggerController.formattedInfo("Sentence: %s", sentence);
        SpellCheckResponse correctedSentence = checkAndSuggestSentence(sentence);
        LoggerController.formattedInfo("Suggested sentence: %s", correctedSentence.toString());
        for (String word : words) {
           checkAndSuggestWord(word);
        }
        return correctedSentence;
    }

}
