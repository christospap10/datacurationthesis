package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.dto.SpellCheckResponse;
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

    // A method to demonstrate the spell checker functionality
    public void testGreekSpellCheck() {
        String[] words = {"καλημέρα", "καλημρα", "καλοκαίρι", "καλογηρία", "απόγμα"};
        for (String word : words) {
           checkAndSuggestWord(word);
        }
    }

}
