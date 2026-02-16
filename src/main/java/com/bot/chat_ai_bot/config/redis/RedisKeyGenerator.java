package com.bot.chat_ai_bot.config.redis;

import org.jspecify.annotations.NonNull;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.bot.chat_ai_bot.config.redis.constants.RedisConstants.PSYCHOLOGY_KEY_GENERATOR;

@Component(PSYCHOLOGY_KEY_GENERATOR)
public class RedisKeyGenerator implements KeyGenerator {

    @Override
    public @NonNull Object generate(@NonNull Object target, @NonNull Method method, Object @NonNull ... params) {
        return "language:" + params[0];
    }

}
