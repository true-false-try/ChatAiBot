package com.bot.chat_ai_bot.service.ai;

import com.bot.chat_ai_bot.dto.AiResponseDto;
import com.bot.chat_ai_bot.dto.prompt.ContextPromptDto;
import com.bot.chat_ai_bot.mapper.AiMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AiService {
    private final List<ChatModel> models;
    private final JpaChatMemory chatMemory;
    private final AiMapper aiMapper;

    public AiResponseDto generateResponse(String chatId, ContextPromptDto contextPromptDto, String userMessage) {
        for (ChatModel model : models) {
            try {
                String response = ChatClient.create(model)
                        .prompt()
                        .system(contextPromptDto.getPromptContext())
                        .user(userMessage)
                        .advisors(new MessageChatMemoryAdvisor(chatMemory, chatId, 20))
                        .call()
                        .content();

                if (response != null && !response.isBlank()) {
                    return aiMapper.toAi(response);
                }
            } catch (Exception ex) {
                log.warn("Model {} failed, trying next: {}", model.getClass().getSimpleName(), ex.getMessage());
            }
        }
        log.error("All AI models failed for chatId: {}", chatId);
        return aiMapper.toAi("I have overload, please try again later...");
    }
}
