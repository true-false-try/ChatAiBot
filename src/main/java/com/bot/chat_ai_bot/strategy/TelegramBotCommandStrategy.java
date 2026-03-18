package com.bot.chat_ai_bot.strategy;


import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramBotCommandStrategy {
    String getCommandName();
    boolean canHandle(String message);
    void execute(Update update, DefaultAbsSender sender) throws TelegramApiException;
}
