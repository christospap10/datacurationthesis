package com.datacurationthesis.datacurationthesis.controller;

import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.timeout.TimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OllamaController {

    private final ChatClient chatClient;

    private final ChatClient openAiChatClient;

    private final RetryTemplate retryTemplate;

    public OllamaController(
            @Qualifier("ollamaChatModel") OllamaChatModel ollamaChatModel,
            @Qualifier("openAiChatModel") OpenAiChatModel openAiChatModel,
            RetryTemplate retryTemplate) {
        this.chatClient = ChatClient.builder(ollamaChatModel).build();
        this.openAiChatClient = ChatClient.builder(openAiChatModel).build();
        this.retryTemplate = retryTemplate;
    }

    @GetMapping("/ollama")
    @Retryable(value = { TimeoutException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public String getCompletion() {
        try {
            ChatResponse chatResponse = chatClient
                    .prompt()
                    .user("Please tell me a joke about computers")
                    .call()
                    .chatResponse();

            LoggerController.formattedInfo(
                    "Model used: " + chatResponse.getResults().toString());
            return retryTemplate.execute(context -> chatResponse.getResult().getOutput().getContent());
        } catch (TimeoutException e) {
            LoggerController.formattedError("Timeout Exception: ", e.getMessage());
            String errorResponse = "Response timed out. Please try again later.";
            return errorResponse;
        }
    }

    @GetMapping("/openai")
    @Retryable(value = { TimeoutException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public String getOpenAIresponse(@RequestParam String title) {
        try {
            String message = """
                    Ποιά είναι η ακριβής διεύθυνση του Θεάτρου ή Κινηματογράφου {title} στην Ελλάδα;
                    Παρακαλώ δώστε την απάντηση στη μορφή: "Η ακριβής διεύθυνση του {title} είναι [ΔΙΕΥΘΥΝΣΗ]."
                    """;
            PromptTemplate template = new PromptTemplate(message);
            Prompt prompt = template.create(Map.of("title", title));
            LoggerController.formattedInfo("Prompt sent to OpenAI API: " + prompt.toString());

            ChatResponse response = openAiChatClient.prompt(prompt).call().chatResponse();
            String fullResponse = response.getResult().getOutput().getContent();
            LoggerController.formattedInfo("Response received from OpenAI API: " + fullResponse);

            String extractedAddress = extractAddress(fullResponse);
            LoggerController.formattedInfo("Extracted Address: " + extractedAddress);
            return extractedAddress;
        } catch (TimeoutException e) {
            LoggerController.formattedError("Timeout Exception: ", e.getMessage());
            String errorResponse = "Response timed out. Please try again later.";
            return errorResponse;
        }
    }

    private String extractAddress(String response) {
        Pattern pattern = Pattern.compile("είναι\\s+(.*)");
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            String address = matcher.group(1).trim();
            int index = address.indexOf("Ελλάδα");
            if (index != -1) {
                address = address.substring(0, index + "Ελλάδα".length()).trim();
            }
            if (address.endsWith(".")) {
                return address.substring(0, address.length() - 1).trim();
            } else {
                return address;
            }
        } else {
            LoggerController.formattedError("Failed to extract the address from the response!");
            return null;
        }
    }

    // The method should take the url from the productionId and return a json with
    // the subroles of the production

    public ResponseEntity<Map<String, String>> getSubrolesFromLLM(@RequestParam String url) {
        try {
            String message = """
                    Παρακαλώ απαντήστε **μόνο** με ένα αντικείμενο JSON που περιέχει τους συντελεστές (subroles) της παράστασης {url}.
                    συμπεριλαμβανομένων των ονομάτων των ατόμων που ανήκουν σε κάθε ρόλο.
                    Δεν χρειάζονται άλλες εξηγήσεις ή κείμενο.
                    """;
            PromptTemplate promptTemplate = new PromptTemplate(message);
            Prompt prompt = promptTemplate.create(Map.of("url", url));
            LoggerController.formattedInfo("Prompt sent to LLM: " + prompt.toString());

            ChatResponse response = openAiChatClient.prompt(prompt).call().chatResponse();
            String fullResponse = response.getResult().getOutput().getContent();
            // Clean the response
            String cleanedResponse = fullResponse
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            LoggerController.formattedInfo("Cleaned Response from LLM: " + cleanedResponse);

            // Parse the cleaned JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> subRolesMap = objectMapper.readValue(cleanedResponse, new TypeReference<>() {
            });

            if (subRolesMap.isEmpty()) {
                LoggerController.formattedInfo("Subroles JSON is empty");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            LoggerController.formattedInfo("Subroles extracted: " + subRolesMap.toString());
            return new ResponseEntity<>(subRolesMap, HttpStatus.OK);

        } catch (TimeoutException e) {
            LoggerController.formattedError("Response timed out. Please try again later!", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            return new ResponseEntity<>(errorResponse, HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            LoggerController.formattedError("Error processing JSON response: " + e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // public ResponseEntity<Map<String, String>> getSubrolesFromLLM(@RequestParam
    // String url) {
    // try {
    // String message = """
    // Παρακαλώ απαντήστε **μόνο** με ένα αντικείμενο JSON που περιέχει τους
    // συντελεστές (subroles) της παράστασης {url},
    // συμπεριλαμβανομένων των ονομάτων των ατόμων που ανήκουν σε κάθε ρόλο.
    // Δεν χρειάζονται άλλες εξηγήσεις ή κείμενο.
    // """;
    // PromptTemplate promptTemplate = new PromptTemplate(message);
    // Prompt prompt = promptTemplate.create(Map.of("url", url));
    // LoggerController.formattedInfo("Prompt sent to LLM: " + prompt.toString());

    // ChatResponse response =
    // openAiChatClient.prompt(prompt).call().chatResponse();
    // String fullResponse = response.getResult().getOutput().getContent();

    // // Clean the response
    // String cleanedResponse = fullResponse
    // .replace("```json", "")
    // .replace("```", "")
    // .trim();

    // LoggerController.formattedInfo("Cleaned Response from LLM: " +
    // cleanedResponse);

    // // Parse the cleaned JSON response
    // ObjectMapper objectMapper = new ObjectMapper();
    // JsonNode jsonNode = objectMapper.readTree(cleanedResponse);

    // // Extract subroles as key-value pairs
    // Map<String, String> subRolesMap = new HashMap<>();
    // JsonNode subRolesArray = jsonNode.get("subroles");

    // if (subRolesArray.isArray()) {
    // for (JsonNode subRoleNode : subRolesArray) {
    // String subRoleEntry = subRoleNode.asText();
    // String[] parts = subRoleEntry.split(":", 2);
    // if (parts.length == 2) {
    // subRolesMap.put(parts[0].trim(), parts[1].trim());
    // }
    // }
    // }

    // if (subRolesMap.isEmpty()) {
    // LoggerController.formattedInfo("Subroles json is empty");
    // return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    // }

    // LoggerController.formattedInfo("Subroles extracted: ",
    // subRolesMap.toString());
    // return new ResponseEntity<>(subRolesMap, HttpStatus.OK);

    // } catch (TimeoutException e) {
    // LoggerController.formattedError("Response timed out. Please try again
    // later!", e.getMessage());
    // Map<String, String> errorResponse = new HashMap<>();
    // errorResponse.put("error", "Response timed out. Please try again later!");
    // return new ResponseEntity<>(errorResponse, HttpStatus.REQUEST_TIMEOUT);
    // } catch (Exception e) {
    // LoggerController.formattedError("Error processing JSON response: " +
    // e.getMessage(), e);
    // Map<String, String> errorResponse = new HashMap<>();
    // errorResponse.put("error", "An error occurred while processing the JSON
    // response.");
    // return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    // }
    // }

}
