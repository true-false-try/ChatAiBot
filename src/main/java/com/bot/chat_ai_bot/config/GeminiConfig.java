package com.bot.chat_ai_bot.config;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {
    @Value("${gemini.token}")
    private String geminiToken;

    @Bean
    Client geminiClient() {
        return Client.builder()
                .apiKey(geminiToken)
                .build();
    }
}
