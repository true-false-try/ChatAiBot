package com.bot.chat_ai_bot.strategy.impl;

import com.bot.chat_ai_bot.service.ai.JpaChatMemory;
import com.bot.chat_ai_bot.service.telegram_bot.TelegramMessageService;
import com.bot.chat_ai_bot.strategy.TelegramBotCommandStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class CommandClearStrategyImpl implements TelegramBotCommandStrategy {
    private final JpaChatMemory jpaChatMemory;
    private final TelegramMessageService messageService;

    @Override
    public String getCommandName() {return "/clear";}

    @Override
    public boolean canHandle(String message) {
        return message.equalsIgnoreCase(getCommandName());
    }

    @Override
    public void execute(Update update, DefaultAbsSender sender) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        jpaChatMemory.clear(chatId);
        sender.execute(messageService.createTextMessage(chatId, "\uD83E\uDDF9 History cleared. Context cleared!"));
    }
}
