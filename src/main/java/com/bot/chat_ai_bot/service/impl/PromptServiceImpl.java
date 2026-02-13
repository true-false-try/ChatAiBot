package com.bot.chat_ai_bot.service.impl;
import com.bot.chat_ai_bot.dto.prompt.ContextPromptDto;
import com.bot.chat_ai_bot.entity.SystemPromptEntity;
import com.bot.chat_ai_bot.repository.SystemPromptRepository;
import com.bot.chat_ai_bot.service.PromptService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.bot.chat_ai_bot.config.redis.constants.RedisConstants.PSYCHOLOGY_KEY_GENERATOR;
import static com.bot.chat_ai_bot.config.redis.constants.RedisConstants.PSYCHOLOGY_MANAGER;
import static com.bot.chat_ai_bot.config.redis.constants.RedisConstants.PSY_BOT;

@Getter
@Service
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService {
    private final SystemPromptRepository systemPromptRepository;

    @Override
    @Cacheable(value = PSY_BOT, keyGenerator = PSYCHOLOGY_KEY_GENERATOR, cacheManager = PSYCHOLOGY_MANAGER)
    public ContextPromptDto createPsychologyContext(String language) {
        Optional<SystemPromptEntity> systemPrompt =
                systemPromptRepository.findById(language);
        if (systemPrompt.isEmpty()) {
            systemPrompt = systemPromptRepository.findById("en");
        }
        return ContextPromptDto.builder()
                .promptContext(
                        systemPrompt.get().getPrompt().toString())
                .build();
    }

}
