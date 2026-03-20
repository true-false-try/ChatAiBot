package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.entity.SessionEntity;
import com.bot.chat_ai_bot.service.LanguageService;
import com.bot.chat_ai_bot.service.SessionService;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.bot.chat_ai_bot.constants.ChatAiConstants.DEFAULT_LANGUAGE;
import static com.bot.chat_ai_bot.constants.ChatAiConstants.FAVORITE_LANGUAGE;
import static com.bot.chat_ai_bot.constants.ChatAiConstants.NOT_PREFERRED_LANGUAGE;

@Service
public class LanguageServiceImpl implements LanguageService {

    @Override
    public String getLanguageFromMessage(String userMessage) {
        List<LanguageProfile> languageProfiles;
        try {
            languageProfiles = new LanguageProfileReader().read(List.of(FAVORITE_LANGUAGE, DEFAULT_LANGUAGE, NOT_PREFERRED_LANGUAGE));
        } catch (IOException ex) {
            return DEFAULT_LANGUAGE;
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
