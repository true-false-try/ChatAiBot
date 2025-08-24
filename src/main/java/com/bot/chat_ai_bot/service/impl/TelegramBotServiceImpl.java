package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.service.GeminiService;
import com.bot.chat_ai_bot.service.TelegramBotService;
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

    private final GeminiService geminiService;

    @Override
    public void update(Map<String, Object> updateMap) {}

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            try {
                Message inMessage = update.getMessage();
                SendMessage outMessage = new SendMessage();

                outMessage.setChatId(inMessage.getChatId());
                outMessage.setText(geminiService.askGemini(inMessage.getText()));

                execute(outMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }
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
