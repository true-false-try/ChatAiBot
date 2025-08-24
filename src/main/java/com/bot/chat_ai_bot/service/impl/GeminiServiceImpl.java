package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.service.GeminiService;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

import static com.bot.chat_ai_bot.constants.ChatAiConstants.GEMINI_MODEL;

@Service
public record GeminiServiceImpl(Client client) implements GeminiService {
    public String askGemini(String prompt) {
        GenerateContentResponse response =
                client.models.generateContent(
                        GEMINI_MODEL,
                        prompt,
                        null
                );
        return response.text();
    }
}
