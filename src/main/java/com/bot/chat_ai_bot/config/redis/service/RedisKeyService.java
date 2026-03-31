package com.bot.chat_ai_bot.config.redis.service;

import org.springframework.stereotype.Component;

import static com.bot.chat_ai_bot.config.redis.constants.RedisConstants.BATCH_PREFIX;
import static com.bot.chat_ai_bot.config.redis.constants.RedisConstants.LANG_PREFIX;

@Component
public class RedisKeyService {

    public String getMoodBatchKey(Long userId) {
        return BATCH_PREFIX + userId;
    }

    public String getLanguageKey(Object param) {
        return LANG_PREFIX + param;
    }
}