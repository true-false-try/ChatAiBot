package com.bot.chat_ai_bot.config.ai;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.ollama.management.PullModelStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalyticsAiConfig {

    @Bean
    public OllamaChatModel analyticsModel(
            @Value("${spring.ai.ollama.base-url}") String url,
            @Value("${spring.ai.ollama.chat.model}") String model
    ) {
        return OllamaChatModel.builder()
                .withOllamaApi(new OllamaApi(url))
                .withDefaultOptions(OllamaOptions.builder()
                        .withModel(model)
                        .withTemperature(0.1)
                        .build())
                .withObservationRegistry(ObservationRegistry.NOOP)
                .withModelManagementOptions(ModelManagementOptions.builder()
                        .withPullModelStrategy(PullModelStrategy.NEVER)
                        .build())
                .build();
    }
}
