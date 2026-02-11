package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.dto.prompt.PsychologyPromptDto;
import com.bot.chat_ai_bot.mapper.TelegramBotMapper;
import com.bot.chat_ai_bot.repository.UserRepository;
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
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
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
                BigInteger userId = BigInteger.valueOf(inMessage.getFrom().getId());

                outSticker.setChatId(inMessage.getChatId().toString());
                outSticker.setSticker(new InputFile(stickerId));
                execute(outSticker);
                outMessage.setChatId(inMessage.getChatId());

                PsychologyPromptDto psychologyPromptDto = promptService.createPsychologyContext(userMessageLanguage, inMessage.getChatId().toString());

                outMessage.setText(geminiService.askGemini(createUserPrompt(psychologyPromptDto, userMessage)));
                execute(outMessage);

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

    private String createUserPrompt(PsychologyPromptDto psychologyPromptDto, String userPrompt) {
        return psychologyPromptDto.getPromptContext().concat("USER_MESSAGE: ").concat(userPrompt);
    }

}
