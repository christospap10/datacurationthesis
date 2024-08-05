package com.datacurationthesis.datacurationthesis.dto;

import java.util.List;

public class SpellCheckResponse {
    private String word;
    private boolean isCorrect;
    private List<String> hunspellSuggestions;
    private String levenshteinSuggestion;

    public SpellCheckResponse(String word, boolean isCorrect, List<String> hunspellSuggestions, String levenshteinSuggestion) {
        this.word = word;
        this.isCorrect = isCorrect;
        this.hunspellSuggestions = hunspellSuggestions;
        this.levenshteinSuggestion = levenshteinSuggestion;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public List<String> getHunspellSuggestions() {
        return hunspellSuggestions;
    }

    public void setHunspellSuggestions(List<String> hunspellSuggestions) {
        this.hunspellSuggestions = hunspellSuggestions;
    }

    public String getLevenshteinSuggestion() {
        return levenshteinSuggestion;
    }

    public void setLevenshteinSuggestion(String levenshteinSuggestion) {
        this.levenshteinSuggestion = levenshteinSuggestion;
    }
}
