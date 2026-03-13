package com.bot.chat_ai_bot.config.ai;

import org.springframework.core.annotation.Order;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiAiConfig {

    @Bean
    @Order(1)
    public OpenAiChatModel groqModel(
            @Value("${groq.uri}") String uri,
            @Value("${groq.token}") String key,
            @Value("${groq.model}") String model) {
        return createModel(uri, key, model);
    }

    @Bean
    @Order(2)
    public OpenAiChatModel cerebrasModel(
            @Value("${cerebras.uri}") String uri,
            @Value("${cerebras.token}") String key,
            @Value("${cerebras.model}") String model) {
        return createModel(uri, key, model);
    }

    @Bean
    @Order(3)
    public OpenAiChatModel geminiModel(
            @Value("${gemini.uri}") String uri,
            @Value("${gemini.token}") String key,
            @Value("${gemini.model}") String model) {
        return createModel(uri, key, model);
    }

    private OpenAiChatModel createModel(String baseUrl, String apiKey, String modelName) {
        var api = new OpenAiApi(baseUrl, apiKey);
        var options = OpenAiChatOptions.builder()
                .withModel(modelName)
                .withTemperature(0.7)
                .build();
        return new OpenAiChatModel(api, options);
    }
}