package com.bot.chat_ai_bot.service.impl;
import com.bot.chat_ai_bot.dto.prompt.PsychologyPromptDto;
import com.bot.chat_ai_bot.entity.PromptEntity;
import com.bot.chat_ai_bot.repository.PromptRedisRepository;
import com.bot.chat_ai_bot.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService {
    private final PromptRedisRepository promptRepository;

    @Override
    public PsychologyPromptDto createPsychologyContext(String userMessage, String language) {
        Optional<PromptEntity> opPromptRedis =
                promptRepository.findById(String.format("psychology:%s", language));
        if (opPromptRedis.isEmpty()) {
            opPromptRedis = promptRepository.findById("psychology:base");
        }

        return PsychologyPromptDto.builder()
                .promptContext(String.format(
                        opPromptRedis.get().toString(),
                        userMessage)
                )
                .build();
    }
}
