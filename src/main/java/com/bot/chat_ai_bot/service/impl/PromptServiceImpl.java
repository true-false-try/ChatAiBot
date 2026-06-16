package com.bot.chat_ai_bot.service.impl;
import com.bot.chat_ai_bot.dto.prompt.ContextPromptDto;
import com.bot.chat_ai_bot.entity.SystemPromptEntity;
import com.bot.chat_ai_bot.repository.SystemPromptRepository;
import com.bot.chat_ai_bot.service.PromptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import static com.bot.chat_ai_bot.config.redis.constants.RedisConstants.PSYCHOLOGY_KEY_GENERATOR;
import static com.bot.chat_ai_bot.config.redis.constants.RedisConstants.PSY_BOT;
import static com.bot.chat_ai_bot.constants.ChatAiConstants.DEFAULT_LANGUAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService {
    private final SystemPromptRepository systemPromptRepository;

    @Override
    @Cacheable(value = PSY_BOT, keyGenerator = PSYCHOLOGY_KEY_GENERATOR)
    public ContextPromptDto createPsychologyContext(String language) {
        SystemPromptEntity systemPrompt = systemPromptRepository.findById(language)
                .orElseGet(() -> {
                    log.debug("No prompt found for language '{}', falling back to 'en'", language);
                    return systemPromptRepository.findById(DEFAULT_LANGUAGE)
                            .orElseThrow(() -> new IllegalStateException("No system prompt for '" + language + "' or default 'en'"));
                });
        log.debug("Creating psychology context, language: {}, promptLength: {}", language, systemPrompt.getPrompt().length());
        return ContextPromptDto.builder()
                .promptContext(systemPrompt.getPrompt())
                .build();
    }



}
