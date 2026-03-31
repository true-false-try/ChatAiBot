package com.bot.chat_ai_bot.service.broker;

import com.bot.chat_ai_bot.config.rabbit_mq.dto.RabbitMqDto;
import com.bot.chat_ai_bot.dto.broker.MoodTaskDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class MoodProducer {
    private final RabbitMqDto rabbitMqDto;
    private final RabbitTemplate rabbitTemplate;

    public void sendMoodRate(MoodTaskDto task) {
        rabbitTemplate.convertAndSend(rabbitMqDto.exchange(), rabbitMqDto.key(), task);
        log.info("Message was sending: {}", task);
    }
}
