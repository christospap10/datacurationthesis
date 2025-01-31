package com.datacurationthesis.datacurationthesis.service;

import java.util.List;
import java.util.Map;

public class ContributionRoleAssigner {

    private final NlpService nlpService; 

    public ContributionRoleAssigner(NlpService nlpService) {
        this.nlpService = nlpService;
    }
    
    public String identifySpecificRole(String contributorName, String productionDescription, String contributorBio) {
        // Combine relevant text for analysis
        StringBuilder combinedText = new StringBuilder();
        if (productionDescription != null && !productionDescription.isEmpty()) {
            combinedText.append(productionDescription).append(" ");
        }
        if (contributorBio != null && !contributorBio.isEmpty()) {
            combinedText.append(contributorBio).append(" ");
        }

        // Analyze the text using the NLP service
        Map<String, List<String>> extractedEntities = nlpService.extractEntities(combinedText.toString());

        // Search for relevant roles in the extracted entities
        String specificRole = null;
        if (extractedEntities.containsKey("Role")) {
            List<String> roles = extractedEntities.get("Role");
            for (String role : roles) {
                if (role.toLowerCase().contains(contributorName.toLowerCase())) {
                    specificRole = role;
                    break;
                }
            }
        }

        // Fallback if no specific role is identified
        if (specificRole == null) {
            specificRole = "General " + (extractedEntities.containsKey("Role") ? extractedEntities.get("Role").get(0) : "Performer");
        }

        return specificRole;
    }
}

