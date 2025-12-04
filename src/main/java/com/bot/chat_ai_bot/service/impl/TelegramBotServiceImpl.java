package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.mapper.TelegramBotMapper;
import com.bot.chat_ai_bot.service.GeminiService;
import com.bot.chat_ai_bot.service.TelegramBotService;
import com.bot.chat_ai_bot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.naming.NotContextException;
import java.math.BigInteger;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramBotServiceImpl extends TelegramLongPollingBot implements TelegramBotService {
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.name}")
    private String botName;

    @Value("${sticker.id}")
    private String stickerId;

    private final GeminiService geminiService;
    private final UserService userService;
    private final TelegramBotMapper telegramBotMapper;

    @Override
    public void update(Map<String, Object> updateMap) {}

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            try {
                Message inMessage = update.getMessage();
                SendMessage outMessage = new SendMessage();
                SendSticker outSticker = new SendSticker();

                outSticker.setChatId(inMessage.getChatId().toString());
                outSticker.setSticker(new InputFile(stickerId));
                execute(outSticker);
                outMessage.setChatId(inMessage.getChatId());

                outMessage.setText(geminiService.askGemini(inMessage.getText()));

                execute(outMessage);

                userService.saveUser(
                        telegramBotMapper.toUserDto(
                                BigInteger.valueOf(inMessage.getFrom().getId()), //userId
                                inMessage.getFrom().getFirstName(),
                                inMessage.getFrom().getLastName(),
                                inMessage.getFrom().getUserName(),
                                Long.valueOf(inMessage.getDate()),
                                inMessage.getFrom().getLanguageCode(),
                                String.valueOf(inMessage.getChat().getId())
                        ),
                        inMessage.getText(),
                        outMessage.getText()
                );

            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
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
