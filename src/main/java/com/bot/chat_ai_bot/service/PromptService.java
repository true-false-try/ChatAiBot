package com.bot.chat_ai_bot.service;

import com.bot.chat_ai_bot.dto.prompt.PsychologyPromptDto;

import static com.bot.chat_ai_bot.constants.ChatAiConstants.DEFAULT_LANGUAGE_PROMPT;

public interface PromptService {
    PsychologyPromptDto createPsychologyContext(String language, String chatId);
}
