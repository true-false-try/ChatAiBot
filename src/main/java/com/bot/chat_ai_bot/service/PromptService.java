package com.bot.chat_ai_bot.service;

import com.bot.chat_ai_bot.dto.prompt.ContextPromptDto;

public interface PromptService {
    ContextPromptDto createPsychologyContext(String language);
}
