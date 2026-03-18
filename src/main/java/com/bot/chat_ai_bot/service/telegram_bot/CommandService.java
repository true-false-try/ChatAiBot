package com.bot.chat_ai_bot.service.telegram_bot;

import com.bot.chat_ai_bot.strategy.TelegramBotCommandStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommandService {
    private final List<TelegramBotCommandStrategy> commandStrategies;

    public boolean handle(Update update, DefaultAbsSender sender) {
        String message = update.getMessage().getText();

        return commandStrategies.stream()
                .filter(cmd -> cmd.canHandle(message))
                .findFirst()
                .map(cmd -> {
                    try {
                        cmd.execute(update, sender);
                    } catch (TelegramApiException ex) {
                        log.error("Command: {} not found.", cmd.getCommandName(), ex);
                    }
                    return true;
                })
                .orElse(false);
    }
}
