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
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
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

    private static final int BATCH_SIZE = 5;
    private static final Duration BATCH_TTL = Duration.ofMinutes(10);

    // Atomically push, check size, and if >= BATCH_SIZE drain and return the list.
    // Returns empty list when batch is not yet full.
    private static final DefaultRedisScript<List> PUSH_AND_DRAIN_SCRIPT = new DefaultRedisScript<>(
            """
            local key = KEYS[1]
            local value = ARGV[1]
            local batchSize = tonumber(ARGV[2])
            local ttlSeconds = tonumber(ARGV[3])
            redis.call('RPUSH', key, value)
            redis.call('EXPIRE', key, ttlSeconds)
            local size = redis.call('LLEN', key)
            if size >= batchSize then
                local items = redis.call('LRANGE', key, 0, -1)
                redis.call('DEL', key)
                return items
            end
            return {}
            """,
            List.class
    );

    @Override
    @Transactional
    public void saveMood(MoodTaskDto task) {
        pushAndDrainBatch(task).ifPresent(batch -> {
            log.info("Batch ready for user {}, size: {}. Analyzing mood...", task.userId(), batch.size());
            Mood detectedMood = ollamaAnalysisService.analyzeMood(batch);

            userRepository.findById(BigInteger.valueOf(task.userId())).ifPresentOrElse(
                    user -> {
                        MoodHistoryEntity entity = new MoodHistoryEntity();
                        entity.setUser(user);
                        entity.setMood(detectedMood);
                        entity.setTriggeredAt(System.currentTimeMillis());
                        moodHistoryRepository.save(entity);
                        log.info("Mood {} saved for user {}", detectedMood, task.userId());
                    },
                    () -> log.warn("User {} not found, mood {} discarded", task.userId(), detectedMood)
            );
        });
    }

    @Override
    public void clearMood(Long userId) {
        moodHistoryRepository.deleteByUserId(userId);
    }

    @SuppressWarnings("unchecked")
    private Optional<List<MoodTaskDto>> pushAndDrainBatch(MoodTaskDto task) {
        String key = redisKeyService.getMoodBatchKey(task.userId());
        String serialized = task.userId() + "|" + task.chatId() + "|"
                + encode(task.userRequest()) + "|" + encode(task.aiResponse());

        List<String> rawItems = (List<String>) redisTemplate.execute(
                PUSH_AND_DRAIN_SCRIPT,
                List.of(key),
                serialized,
                String.valueOf(BATCH_SIZE),
                String.valueOf(BATCH_TTL.getSeconds())
        );

        if (rawItems == null || rawItems.isEmpty()) {
            return Optional.empty();
        }

        List<MoodTaskDto> batch = rawItems.stream()
                .map(this::deserialize)
                .toList();
        return Optional.of(batch);
    }

    private String encode(String value) {
        return value == null ? "" : value.replace("|", "\\|");
    }

    private MoodTaskDto deserialize(Object raw) {
        String s = raw.toString();
        String[] parts = s.split("(?<!\\\\)\\|", 4);
        return new MoodTaskDto(
                Long.parseLong(parts[0]),
                parts.length > 2 ? parts[2].replace("\\|", "|") : "",
                parts.length > 3 ? parts[3].replace("\\|", "|") : "",
                Long.parseLong(parts[1])
        );
    }
}
