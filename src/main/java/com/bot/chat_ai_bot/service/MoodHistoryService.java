package com.bot.chat_ai_bot.service;

import com.bot.chat_ai_bot.dto.broker.MoodTaskDto;

public interface MoodHistoryService {
    void saveMood(MoodTaskDto task);
    void clearMood(Long userId);
}
