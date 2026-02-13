package com.bot.chat_ai_bot.service;

import com.bot.chat_ai_bot.dto.prompt.UserPromptDto;

public interface GeminiService  {
    String askGemini(UserPromptDto userPromptDto);
}
