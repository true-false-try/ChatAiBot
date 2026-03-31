package com.bot.chat_ai_bot.dto.broker;

public record MoodTaskDto(
        Long userId,
        String userRequest,
        String aiResponse,
        Long chatId
) {}