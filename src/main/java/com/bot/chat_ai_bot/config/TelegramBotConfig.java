package com.bot.chat_ai_bot.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.key}")
    private String botKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
