package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.service.LanguageService;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.bot.chat_ai_bot.constants.ChatAiConstants.DEFAULT_LANGUAGE;
import static com.bot.chat_ai_bot.constants.ChatAiConstants.FAVORITE_LANGUAGE;
import static com.bot.chat_ai_bot.constants.ChatAiConstants.NOT_PREFERRED_LANGUAGE;

@Slf4j
@Service
public class LanguageServiceImpl implements LanguageService {

    private final LanguageDetector languageDetector;
    private final TextObjectFactory textObjectFactory;

    public LanguageServiceImpl() {
        List<LanguageProfile> profiles;
        try {
            profiles = new LanguageProfileReader().read(List.of(FAVORITE_LANGUAGE, DEFAULT_LANGUAGE, NOT_PREFERRED_LANGUAGE));
        } catch (IOException ex) {
            log.error("Failed to load language profiles, detection will always return default", ex);
            profiles = List.of();
        }
        this.languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(profiles)
                .build();
        this.textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
    }

    @Override
    public String getLanguageFromMessage(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return DEFAULT_LANGUAGE;
        }
        TextObject textObject = textObjectFactory.forText(userMessage);
        com.google.common.base.Optional<LdLocale> detected = languageDetector.detect(textObject);
        return detected.isPresent() ? detected.get().getLanguage() : DEFAULT_LANGUAGE;
    }
}
