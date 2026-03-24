package com.bot.chat_ai_bot.config.rabbit_mq.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record RabbitMqDto(
        @Value("${rebbitmq.queue.mood}") String queueMood,
        @Value("${rebbitmq.exchange}") String exchange,
        @Value("${rebbitmq.key}") String key
){}
