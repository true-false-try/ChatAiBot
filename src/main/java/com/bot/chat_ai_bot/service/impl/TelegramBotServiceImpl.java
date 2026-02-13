package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.config.redis.RedisKeyGenerator;
import com.bot.chat_ai_bot.dto.prompt.ContextPromptDto;
import com.bot.chat_ai_bot.dto.prompt.UserPromptDto;
import com.bot.chat_ai_bot.mapper.TelegramBotMapper;
import com.bot.chat_ai_bot.service.GeminiService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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

import static com.bot.chat_ai_bot.config.redis.constants.RedisConstants.PSY_BOT;
import static com.bot.chat_ai_bot.constants.ChatAiConstants.DEFAULT_LANGUAGE_PROMPT;
import static com.bot.chat_ai_bot.constants.ChatAiConstants.NOT_PREFERRED_LANGUAGE;

@Service
@RequiredArgsConstructor
public class TelegramBotServiceImpl extends TelegramLongPollingBot implements TelegramBotService {
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.name}")
    private String botName;

    @Value("${sticker.id}")
    private String stickerId;

    private static final String USER_MESSAGE = " USER_MESSAGE: ";

    private final GeminiService geminiService;
    private final UserService userService;
    private final TelegramBotMapper telegramBotMapper;
    private final PromptService promptService;
    private final RedisKeyGenerator redisKeyGenerator;
    private final CacheManager cacheManager;



    @Override
    public void update(Map<String, Object> updateMap) {}

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            try {
                Message inMessage = update.getMessage();
                SendMessage outMessage = new SendMessage();
                SendSticker outSticker = new SendSticker();

                String chatId = inMessage.getChatId().toString();
                String userMessage = inMessage.getText();
                String userMessageLanguage = getLanguageFromMessage(userMessage);
                BigInteger userId = BigInteger.valueOf(inMessage.getFrom().getId());

                outSticker.setChatId(chatId);
                outSticker.setSticker(new InputFile(stickerId));
                Message stickerMsg = execute(outSticker);
                outMessage.setChatId(inMessage.getChatId());


                boolean hasCacheKey = checkedKeyIsInCache(userMessageLanguage);
                ContextPromptDto contextPromptDto = promptService.createPsychologyContext(userMessageLanguage);
                outMessage.setText(geminiService.askGemini(createUserPrompt(contextPromptDto, userMessage, hasCacheKey)));
                execute(outMessage);

                DeleteMessage deleteSticker = new DeleteMessage();
                deleteSticker.setChatId(chatId);
                deleteSticker.setMessageId(stickerMsg.getMessageId());
                execute(deleteSticker);

                userService.saveUser(
                        telegramBotMapper.toUserDto(
                                userId,
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

    private UserPromptDto createUserPrompt(ContextPromptDto contextPromptDto, String userPrompt, boolean hasCacheKey) {
        if (hasCacheKey) {
            return UserPromptDto.builder()
                    .userPromptContext(USER_MESSAGE.concat(userPrompt))
                    .build();
        } else
            return UserPromptDto.builder()
                    .userPromptContext(contextPromptDto.getPromptContext()
                            .concat(USER_MESSAGE
                                    .concat(userPrompt)))
                    .build();
    }

    private boolean checkedKeyIsInCache(String userMessageLanguage) {
        Cache cache = cacheManager.getCache(PSY_BOT);
        if (cache == null) return false;
        Object generatedKey = redisKeyGenerator.generate(this, null, userMessageLanguage);
        return cache.get(generatedKey) != null;
    }

}
