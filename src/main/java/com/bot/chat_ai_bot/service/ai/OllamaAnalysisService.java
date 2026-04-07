package com.bot.chat_ai_bot.service.ai;

import com.bot.chat_ai_bot.dto.broker.MoodTaskDto;
import com.bot.chat_ai_bot.entity.Mood;

import java.util.List;

public interface OllamaAnalysisService {
    Mood analyzeMood(List<MoodTaskDto> moodTasks);
}
