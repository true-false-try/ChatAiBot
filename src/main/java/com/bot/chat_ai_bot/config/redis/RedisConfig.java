package com.bot.chat_ai_bot.config.redis;

import com.bot.chat_ai_bot.dto.broker.MoodTaskDto;
import com.bot.chat_ai_bot.dto.prompt.ContextPromptDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    /**
     * General-purpose template for miscellaneous Object values (caching, etc).
     * Uses GenericJackson2JsonRedisSerializer which embeds @class metadata so any type round-trips correctly.
     */
    @Bean
    public RedisTemplate<String, Object> genericRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    /**
     * Typed template for the mood-batch queue.
     * Uses Jackson2JsonRedisSerializer so stored JSON is clean (no @class noise),
     * and Spring auto-deserializes Lua script results back to MoodTaskDto.
     */
    @Bean
    public RedisTemplate<String, MoodTaskDto> moodBatchRedisTemplate(RedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<MoodTaskDto> serializer =
                new Jackson2JsonRedisSerializer<>(MoodTaskDto.class);

        RedisTemplate<String, MoodTaskDto> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        return template;
    }

    /**
     * Cache config for @Cacheable (PromptService system-prompt cache).
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new Jackson2JsonRedisSerializer<>(ContextPromptDto.class)));
    }
}
