package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import org.languagetool.JLanguageTool;
import org.languagetool.Languages;
import org.languagetool.language.English;
import org.languagetool.language.Greek;
import org.languagetool.rules.RuleMatch;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class SpellCheckService {

    private final JLanguageTool greekLanguageTool;
//    private final JLanguageTool englishLanguageTool;

    public SpellCheckService() {
        this.greekLanguageTool = new JLanguageTool(new Greek());
//        this.englishLanguageTool = new JLanguageTool(new English());
    }

    public boolean isValidWord(String word) {
       try {
           List<RuleMatch> greekrMatches = greekLanguageTool.check(word);
//           List<RuleMatch> englishMatches = englishLanguageTool.check(word);
           return greekrMatches.isEmpty();
       } catch (IOException e) {
           LoggerController.formattedError("Exception while checking if word is valid", e.getMessage());
           throw new RuntimeException(e);
       }
    }
}

