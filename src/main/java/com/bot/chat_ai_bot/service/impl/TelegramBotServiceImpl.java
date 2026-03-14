package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.dto.prompt.ContextPromptDto;
import com.bot.chat_ai_bot.mapper.TelegramBotMapper;
import com.bot.chat_ai_bot.service.ai.AiService;
import com.bot.chat_ai_bot.service.PromptService;
import com.bot.chat_ai_bot.service.TelegramBotService;
import com.bot.chat_ai_bot.service.UserService;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.bot.chat_ai_bot.constants.ChatAiConstants.DEFAULT_LANGUAGE_PROMPT;
import static com.bot.chat_ai_bot.constants.ChatAiConstants.NOT_PREFERRED_LANGUAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotServiceImpl extends TelegramLongPollingBot implements TelegramBotService {
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.name}")
    private String botName;

    @Value("${sticker.id}")
    private String stickerId;

    private final AiService aiService;
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
                String chatId = inMessage.getChatId().toString();
                String userMessage = inMessage.getText();

                if (isCommandHandled(userMessage, chatId)) {
                    return;
                }

                String lang = getLanguageFromMessage(userMessage);

                Message stickerMsg = execute(new SendSticker(chatId, new InputFile(stickerId)));

                ContextPromptDto context = promptService.createPsychologyContext(lang);

                String aiResponse = aiService.generateResponse(
                        chatId,
                        context,
                        userMessage
                );

                execute(new SendMessage(chatId, aiResponse));

                execute(new DeleteMessage(chatId, stickerMsg.getMessageId()));

                saveUserData(inMessage, userMessage, aiResponse, lang);

            } catch (TelegramApiException ex) {
                log.error("Telegram error", ex);
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

    private void saveUserData(Message inMessage, String request, String response, String language) {
        userService.saveUser(
                telegramBotMapper.toUserDto(
                        BigInteger.valueOf(inMessage.getFrom().getId()),
                        inMessage.getFrom().getFirstName(),
                        inMessage.getFrom().getLastName(),
                        inMessage.getFrom().getUserName(),
                        Long.valueOf(inMessage.getDate()),
                        language,
                        inMessage.getChatId().toString()
                ),
                request, response, language
        );
    }

    private boolean isCommandHandled(String userMessage, String chatId) throws TelegramApiException {
        if (userMessage.equalsIgnoreCase("/clear")) {
            aiService.clearHistory(chatId);
            execute(new SendMessage(chatId, "\uD83E\uDDF9 History cleared. Context cleared!"));
            return true;
        }
        return false;
    }

    private String getLanguageFromMessage(String userMessage) {
        List<LanguageProfile> languageProfiles;
        try {
            languageProfiles = new LanguageProfileReader().readAllBuiltIn();
        } catch (IOException ex) {
            return DEFAULT_LANGUAGE_PROMPT;
        }

        LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();

        TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

        TextObject textObject = textObjectFactory.forText(userMessage);
        Optional<LdLocale> detectedLocaleOptional = languageDetector.detect(textObject);

        return detectedLocaleOptional.isPresent() ?
                detectedLocaleOptional.get().getLanguage() :
                NOT_PREFERRED_LANGUAGE;
    }
}
