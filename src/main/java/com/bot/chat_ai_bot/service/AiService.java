package com.bot.chat_ai_bot.service;

import com.bot.chat_ai_bot.dto.prompt.ContextPromptDto;
import com.bot.chat_ai_bot.repository.SystemPromptRepository;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AiService {
    private final ChatClient chatClient;
    private final SystemPromptRepository systemPromptRepository;

    public String generateResponse(Long chatId, ContextPromptDto contextPromptDto, String userPrompt){
        return chatClient.prompt()
                .system(contextPromptDto.getPromptContext())
                .user(userPrompt)
                .advisors(a -> a
                        .param(AbstractChatMemoryAdvisor.DEFAULT_CHAT_MEMORY_CONVERSATION_ID, chatId)
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .content();
    }
}
