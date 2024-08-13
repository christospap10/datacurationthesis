package com.datacurationthesis.datacurationthesis.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OllamaController {

    private final ChatClient chatClient;

    public OllamaController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/ollama")
    public ChatResponse getCompletion() {
        ChatResponse chatResponse =  chatClient.prompt()
                .user("Please tell me a joke about computers")
                .call()
                .chatResponse();
        return chatResponse;
    }
}
