package com.bot.chat_ai_bot.config.redis;

import com.bot.chat_ai_bot.config.redis.service.RedisKeyService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.bot.chat_ai_bot.config.redis.constants.RedisConstants.PSYCHOLOGY_KEY_GENERATOR;

@Component(PSYCHOLOGY_KEY_GENERATOR)
@RequiredArgsConstructor
public class RedisKeyCacheGenerator implements KeyGenerator {
    private final RedisKeyService redisKeyService;

    @Override
    public @NonNull Object generate(@NonNull Object target, @NonNull Method method, Object @NonNull ... params) {
        return redisKeyService.getLanguageKey(params[0]);
    }

}
