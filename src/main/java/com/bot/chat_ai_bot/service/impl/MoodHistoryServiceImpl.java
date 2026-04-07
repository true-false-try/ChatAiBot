package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.config.redis.service.RedisKeyService;
import com.bot.chat_ai_bot.dto.broker.MoodTaskDto;
import com.bot.chat_ai_bot.entity.Mood;
import com.bot.chat_ai_bot.entity.MoodHistoryEntity;
import com.bot.chat_ai_bot.repository.MoodHistoryRepository;
import com.bot.chat_ai_bot.repository.UserRepository;
import com.bot.chat_ai_bot.service.MoodHistoryService;
import com.bot.chat_ai_bot.service.ai.OllamaAnalysisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoodHistoryServiceImpl implements MoodHistoryService {
    private final MoodHistoryRepository moodHistoryRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyService redisKeyService;
    private final OllamaAnalysisService ollamaAnalysisService;

    @Override
    @Transactional
    public void saveMood(MoodTaskDto task) {
        addAndCheckBatch(task).ifPresent(batch -> {
            log.info("Batch ready for user {}. Analyzing...", task.userId());

            Mood detectedMood = ollamaAnalysisService.analyzeMood(batch);

            userRepository.findById(BigInteger.valueOf(task.userId())).ifPresent(user -> {
                MoodHistoryEntity entity = new MoodHistoryEntity();
                entity.setUser(user);
                entity.setMood(detectedMood);
                entity.setTriggeredAt(System.currentTimeMillis());

                moodHistoryRepository.save(entity);
                log.info("Mood {} saved for user {}", detectedMood, task.userId());
            });
        });
    }

    private Optional<List<MoodTaskDto>> addAndCheckBatch(MoodTaskDto task) {
        String key = redisKeyService.getMoodBatchKey(task.userId());
        redisTemplate.opsForList().rightPush(key, task);
        redisTemplate.expire(key, Duration.ofMinutes(10));
        Long currentSize = redisTemplate.opsForList().size(key);

        int BATCH_SIZE = 5;
        if (currentSize!= null && currentSize >= BATCH_SIZE) {
            List<Object> rawBatch = redisTemplate.opsForList().range(key, 0, -1);
            redisTemplate.delete(key);
            List<MoodTaskDto> batch = Objects.requireNonNull(rawBatch).stream()
                    .map(obj -> (MoodTaskDto) obj)
                    .toList();
            return Optional.of(batch);
        }
        return Optional.empty();
    }

}
