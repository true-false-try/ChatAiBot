package com.bot.chataibot;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@TestConfiguration
public class TelegramTestConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        return Mockito.mock(TelegramBotsApi.class);
    }
}
