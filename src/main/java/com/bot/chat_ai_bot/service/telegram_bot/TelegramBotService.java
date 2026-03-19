package com.bot.chat_ai_bot.service.telegram_bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.name}")
    private String botName;

    private final CommandService commandService;
    private final TelegramMessageService telegramMessageService;

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            Message inMessage = update.getMessage();
            if (commandService.handle(update, this)) {return;}
            Message stickerMessage = null;
            try {
                stickerMessage = execute(telegramMessageService.createStickerMessage(inMessage));
                SendMessage aiResponse = telegramMessageService.processAiResponse(inMessage);
                execute(aiResponse);
            } catch (TelegramApiException ex) {
                log.error("Error processing message from chatId: {}", inMessage.getChatId(), ex);
            } finally {
                if (stickerMessage != null) {
                    try {
                        execute(telegramMessageService.removeStickerMessage(stickerMessage));
                    }catch (TelegramApiException e) {
                        log.error("Failed to delete sticker", e);
                    }
                }
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

}
