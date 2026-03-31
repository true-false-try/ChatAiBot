package com.bot.chat_ai_bot.service.broker;

import com.bot.chat_ai_bot.dto.broker.MoodTaskDto;
import com.bot.chat_ai_bot.service.MoodHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MoodConsumer {
    private final MoodHistoryService moodHistoryService;

    @RabbitListener(queues = "#{@rabbitMqDto.queueMood()}", concurrency = "3")
    public void consumeMoodTask(MoodTaskDto task) {
        log.info("Received task from RabbitMQ for user: {}", task.userId());
        try {
            moodHistoryService.saveMood(task);
        } catch (Exception e) {
            log.error("Error processing mood task for user {}: {}", task.userId(), e.getMessage());
        }
    }
}
