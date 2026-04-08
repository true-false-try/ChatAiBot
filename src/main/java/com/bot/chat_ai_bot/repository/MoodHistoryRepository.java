package com.bot.chat_ai_bot.repository;

import com.bot.chat_ai_bot.entity.MoodHistoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodHistoryRepository extends JpaRepository<MoodHistoryEntity, Long> {
    void deleteByUserId(Long userId);
}
