package com.datacurationthesis.datacurationthesis.configuration;

import com.datacurationthesis.datacurationthesis.logger.LoggerController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        LoggerController.info("Retrying request!");
        return  RetryTemplate.builder()
                .maxAttempts(3)
                .fixedBackoff(2000)
                .build();
    }
}
