package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.mapper.TelegramBotMapper;
import com.bot.chat_ai_bot.service.GeminiService;
import com.bot.chat_ai_bot.service.PromptService;
import com.bot.chat_ai_bot.service.TelegramBotService;
import com.bot.chat_ai_bot.service.UserService;
import com.github.pemistahl.lingua.api.IsoCode639_1;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
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



import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

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
    private final PromptService promptService;

    @Override
    public void update(Map<String, Object> updateMap) {}

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            try {
                Message inMessage = update.getMessage();
                SendMessage outMessage = new SendMessage();
                SendSticker outSticker = new SendSticker();
                String userMessage = inMessage.getText();
                String userMessageLanguage = getLanguageFromMessage(userMessage);

                outSticker.setChatId(inMessage.getChatId().toString());
                outSticker.setSticker(new InputFile(stickerId));
                execute(outSticker);
                outMessage.setChatId(inMessage.getChatId());
                String message = geminiService.askGemini(promptService.convertPromptContext(userMessage, userMessageLanguage));

                outMessage.setText(geminiService.askGemini(message));

                execute(outMessage);

                userService.saveUser(
                        telegramBotMapper.toUserDto(
                                BigInteger.valueOf(inMessage.getFrom().getId()), //userId
                                inMessage.getFrom().getFirstName(),
                                inMessage.getFrom().getLastName(),
                                inMessage.getFrom().getUserName(),
                                Long.valueOf(inMessage.getDate()),
                                userMessageLanguage,
                                String.valueOf(inMessage.getChat().getId())
                        ),
                        userMessage,
                        outMessage.getText(),
                        getLanguageFromMessage(userMessage)
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

    private String getLanguageFromMessage(String userMessage) {
        LanguageDetector detector = LanguageDetectorBuilder.fromAllLanguages().build();
        Language language = detector.detectLanguageOf(userMessage);
        Optional<IsoCode639_1> isoCode6391Optional = Optional.of(language.getIsoCode639_1());
        return isoCode6391Optional.toString(); //SOTHO when we insert one or two words
    }

}
