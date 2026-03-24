package com.bot.chat_ai_bot.service.broker;

import com.bot.chat_ai_bot.config.rabbit_mq.dto.RabbitMqDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class MoodConsumer {
    private final RabbitMqDto rabbitMqDto;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "#{@rabbitMqDto.queueMood()}")
    public void consumeMood(String message) {
        try {
            log.info("Handled message: {}", message);

            processRating(message);

        } catch (Exception ex) {
            log.error("Handling exception message: {}", ex.getMessage());
        }
    }

    private void processRating(String data) {

    }
}
