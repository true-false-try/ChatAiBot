package com.bot.chat_ai_bot.service.ai;

import com.bot.chat_ai_bot.dto.prompt.ContextPromptDto;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AiService {
    private final List<ChatModel> models;
    private final JpaChatMemory chatMemory;

    public String generateResponse(Long chatId, ContextPromptDto contextPromptDto, String userPrompt){
        for (ChatModel model : models) {
            try {
                String response = ChatClient.create(model)
                        .prompt()
                        .system(contextPromptDto.getPromptContext())
                        .user(userPrompt)
                        .advisors(new MessageChatMemoryAdvisor(chatMemory, String.valueOf(chatId), 20))
                        .call()
                        .content();

                if (response!= null && !response.isBlank()) {
                    return response;
                }
            } catch (Exception ex) {
                System.err.println("Model failed: " + model.getClass().getSimpleName());
                ex.printStackTrace();
            }
        }
        return "I have overload, please try again later...";
    }
}
