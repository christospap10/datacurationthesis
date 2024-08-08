package com.datacurationthesis.datacurationthesis.dto;

import java.util.List;

public class SpellCheckResponseBasic {
    private String originalText;
    private List<String> suggestions;

    public SpellCheckResponseBasic(String originalText, List<String> suggestions) {
        this.originalText = originalText;
        this.suggestions = suggestions;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}
