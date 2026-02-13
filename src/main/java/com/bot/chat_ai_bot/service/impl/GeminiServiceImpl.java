package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.dto.prompt.UserPromptDto;
import com.bot.chat_ai_bot.service.GeminiService;
import com.google.genai.Client;
import org.springframework.stereotype.Service;

import static com.bot.chat_ai_bot.constants.ChatAiConstants.GEMINI_MODEL;

@Service
public record GeminiServiceImpl(Client client) implements GeminiService {

    public String askGemini(UserPromptDto userPromptDto) {
        return client.models.generateContent(
                GEMINI_MODEL,
                userPromptDto.getUserPromptContext(),
                null
        ).text();
    }
}
