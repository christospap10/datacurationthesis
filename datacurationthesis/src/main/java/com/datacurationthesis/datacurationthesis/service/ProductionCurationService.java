package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Production;
import java.util.regex.Pattern;


public class ProductionCurationService {

    private static final Pattern SPECIAL_CHARACTERS_PATTERN = Pattern.compile("[^\\p{L}\\p{N}\\s]");
    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile("\\s{2,}");
    private static final Pattern STAR_PATTERN = Pattern.compile("\\*");
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\\r?\\n");

    public Production cleanProduction(Production production) {
        // Clean and standardize the title
        if (production.getTitle() != null) {
            String title = production.getTitle();
            title = SPECIAL_CHARACTERS_PATTERN.matcher(title).replaceAll(""); // Remove special characters
            title = MULTIPLE_SPACES_PATTERN.matcher(title).replaceAll(" ");   // Replace multiple spaces
            title = capitalizeWords(title);                                  // Capitalize each word
            production.setTitle(title.trim());
        }

        // Clean and format the description
        if (production.getDescription() != null) {
            String description = production.getDescription();
            description = STAR_PATTERN.matcher(description).replaceAll("");  // Remove stars (*)
            description = NEWLINE_PATTERN.matcher(description).replaceAll(" "); // Remove newlines
            description = MULTIPLE_SPACES_PATTERN.matcher(description).replaceAll(" "); // Remove extra spaces
            production.setDescription(description.trim());
        }

        // Normalize producer name
        if (production.getProducer() != null) {
            String producer = production.getProducer();
            producer = SPECIAL_CHARACTERS_PATTERN.matcher(producer).replaceAll(""); // Remove special characters
            producer = capitalizeWords(producer);                                   // Capitalize each word
            production.setProducer(producer.trim());
        }

        return production;
    }

    // Helper method to capitalize the first letter of each word
    private String capitalizeWords(String str) {
        String[] words = str.split("\\s+");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                           .append(word.substring(1).toLowerCase())
                           .append(" ");
            }
        }
        return capitalized.toString().trim();
    }
}


