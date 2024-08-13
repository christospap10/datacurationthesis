package com.datacurationthesis.datacurationthesis.configuration;

import io.netty.channel.ChannelOption;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class OllamaConfig {

    @Bean
    public ChatClient.Builder chatClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(300)) // Increase response timeout to 30 seconds
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300000); // Increase connect timeout to 10 seconds

        WebClient.Builder webClientBuilder = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));

        return ChatClient.builder((ChatModel) webClientBuilder);
    }
}
