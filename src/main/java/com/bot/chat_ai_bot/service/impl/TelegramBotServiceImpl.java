package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.config.TelegramBotConfig;
import com.bot.chat_ai_bot.service.TelegramBotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class TelegramBotServiceImpl extends TelegramLongPollingBot implements TelegramBotService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.name}")
    private String botName;


    @Override
    public void update(Map<String, Object> updateMap) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Update update = mapper.convertValue(updateMap, Update.class);

            Thread.ofVirtual().start(() -> processUpdate(update));
        } catch (IllegalArgumentException e) {
            System.err.println("Parsing error: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Thread.ofVirtual().start(() -> processUpdate(update));
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void processUpdate(Update update) {
        try {
            if(update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String incomingText = update.getMessage().getText();
                SendMessage message = new SendMessage(chatId, "You are written: " + incomingText);
                execute(message);
            }
        } catch (TelegramApiException ex) {
            System.err.println("System error: " + ex.getMessage());

        }
    }
}
