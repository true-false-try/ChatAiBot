package com.bot.chat_ai_bot.controller;

import com.bot.chat_ai_bot.facade.TelegramChatFacade;
import com.bot.chat_ai_bot.service.telegram_bot.TelegramCommandService;
import com.bot.chat_ai_bot.service.telegram_bot.TelegramMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TelegramBotController extends TelegramLongPollingBot {
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.name}")
    private String botName;

    private final TelegramCommandService commandService;
    private final TelegramMessageService messageService;
    private final TelegramChatFacade chatFacade;

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            Message inMessage = update.getMessage();
            if (commandService.handle(update, this)) {return;}

            Message stickerMessage = null;
            try {
                stickerMessage = execute(messageService.createStickerMessage(inMessage));
                String textResponse = chatFacade.processRequest(inMessage);
                SendMessage messageToExecute = messageService.createTextMessage(
                        inMessage.getChatId().toString(),
                        textResponse
                );
                execute(messageToExecute);
            } catch (TelegramApiException ex) {
                log.error("Error processing message from chatId: {}", inMessage.getChatId(), ex);
            } finally {
               handleStickerMessage(stickerMessage);
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

    private void handleStickerMessage(Message stickerMessage) {
        if (stickerMessage != null) {
            try {
                execute(messageService.removeStickerMessage(stickerMessage));
            }catch (TelegramApiException e) {
                log.error("Failed to delete sticker", e);
            }
        }
    }

}
