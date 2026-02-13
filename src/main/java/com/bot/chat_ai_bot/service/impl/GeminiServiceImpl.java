package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.dto.prompt.UserPromptDto;
import com.bot.chat_ai_bot.service.GeminiService;
import com.google.genai.Client;
import com.google.genai.errors.ClientException;
import org.springframework.stereotype.Service;

import static com.bot.chat_ai_bot.constants.ChatAiConstants.GEMINI_MODEL;

@Service
public record GeminiServiceImpl(Client client) implements GeminiService {

    public String askGemini(UserPromptDto userPromptDto) {
        String text = "";
        try {
              text =  client.models.generateContent(
                     GEMINI_MODEL,
                     userPromptDto.getUserPromptContext(),
                     null
             ).text();
        } catch (ClientException ex) {
            ex.printStackTrace();
        }
        return text;
    }
}
