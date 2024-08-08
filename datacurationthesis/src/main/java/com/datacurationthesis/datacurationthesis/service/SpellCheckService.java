package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.dto.SpellCheckResponseBasic;
import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import org.apache.juli.logging.Log;
import org.languagetool.JLanguageTool;
import org.languagetool.Languages;
import org.languagetool.language.English;
import org.languagetool.language.Greek;
import org.languagetool.rules.RuleMatch;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class SpellCheckService {

    private final JLanguageTool greekLanguageTool;
    private final JLanguageTool englishLanguageTool;

    public SpellCheckService() {
        this.greekLanguageTool = new JLanguageTool(new Greek());
        this.englishLanguageTool = new JLanguageTool(new English());
    }

    public boolean isValidWord(String word) {
       try {
           boolean isGreek = isGreekText(word);

           if (!isGreek) {
               LoggerController.formattedInfo("Word is not Greek: %s", word);
           } else {
               LoggerController.formattedInfo("Word is Greek: %s", word);
           }
           List<RuleMatch> matches = isGreek ? greekLanguageTool.check(word): englishLanguageTool.check(word);
           return matches.isEmpty();
       } catch (IOException e) {
           LoggerController.formattedError("Exception while checking if word is valid", e.getMessage());
           throw new RuntimeException(e);
       }
    }

    public String autoCorrect(String text) {
        try {
        boolean isGreek = isGreekText(text);
        List<RuleMatch> matches = isGreek ? greekLanguageTool.check(text): englishLanguageTool.check(text);
        List<String> allSuggestions = new ArrayList<>();
        String correctedText = applyCorrections(text, matches);
        LoggerController.formattedInfo("Suggested corrections: %s", matches.toString());
        allSuggestions.addAll(matches.stream().map(RuleMatch::getSuggestedReplacements).flatMap(List::stream).toList());
        LoggerController.formattedInfo("All suggestions: %s", allSuggestions.toString());
        return correctedText;
        } catch (IOException e) {
            LoggerController.formattedError("Exception while checking if word is valid", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String applyCorrections(String text, List<RuleMatch> matches) {
        StringBuilder correctedText = new StringBuilder(text);
        int offsetCorrextion = 0;
        Scanner scanner = new Scanner(System.in);

        for (RuleMatch match : matches) {
            List<String> suggestions = match.getSuggestedReplacements();
            if (!suggestions.isEmpty()) {
                LoggerController.formattedInfo("Word: %s", text.substring(match.getFromPos(), match.getToPos()));
                LoggerController.formattedInfo("Suggestions:");
                for (int i = 0; i < suggestions.size(); i++) {
                    System.out.printf("%d. %s%n", i + 1, suggestions.get(i));
                }
                System.out.print("Enter the number of the suggestion you want to apply (or press enter to skip): ");
                String input = scanner.nextLine();
                try {
                    int choice = Integer.parseInt(input) - 1;
                    if (choice >= 0 && choice < suggestions.size()) {
                       String suggestion = suggestions.get(choice);
                       int start = match.getFromPos() + offsetCorrextion;
                       int end = match.getToPos() + offsetCorrextion;

                       if (start >= 0 && end <= correctedText.length() && start <= end) {
                          correctedText.replace(start, end, suggestion);
                          offsetCorrextion += suggestion.length() - (end - start);
                       }
                    }
                } catch (NumberFormatException e) {
                    System.out.print("No valid suggestion selected. Enter your correction (or press enter to keep original): ");
                    String customCorrection = scanner.nextLine().trim();
                    if (!customCorrection.isEmpty()) {
                        int start = match.getFromPos() + offsetCorrextion;
                        int end = match.getToPos() + offsetCorrextion;
                        correctedText.replace(start, end, customCorrection);
                        offsetCorrextion += customCorrection.length() - (end - start);
                    } else {
                        LoggerController.formattedInfo("Keeping original word!");
                    }
                }
            }
        }
        return correctedText.toString();
    }



//
//    private String applyCorrections(String text, List<RuleMatch> matches) {
//       StringBuilder correctedText = new StringBuilder(text);
//       int offsetCorrection = 0;
//        Scanner scanner = new Scanner(System.in);
//       for (RuleMatch match: matches) {
//           Optional<String> suggestion = match.getSuggestedReplacements().stream().findFirst();
//           if (suggestion.isPresent()) {
//               int start = match.getFromPos() + offsetCorrection;
//               int end = match.getToPos() + offsetCorrection;
//
//               if (start >= 0 && end <= correctedText.length() && start <= end) {
//                   correctedText.replace(start, end, suggestion.get());
//                   offsetCorrection += suggestion.get().length() - (end - start);
//               }
//           }
//       }
//       return correctedText.toString();
//    }


    public SpellCheckResponseBasic checkAndSuggest(String text) {
        boolean isGreek = isGreekText(text);
        JLanguageTool languageTool = isGreek ? greekLanguageTool : englishLanguageTool;

        try {
            List<RuleMatch> matches = languageTool.check(text);
            List<String> suggestions = new ArrayList<>();

            for (RuleMatch match : matches) {
                suggestions.addAll(match.getSuggestedReplacements());
            }

            return new SpellCheckResponseBasic(text, suggestions);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Spell check failed");
        }
    }
    public boolean isGreekText(String text) {
        return text.chars().anyMatch(c -> (c >= 0x0370 && c <= 0x03FF) || (c >= 0x1F00 && c <= 0x1FFF));
    }

    public boolean isEnglishText(String text) {
        return text.chars().anyMatch(c -> (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'));
    }
}

