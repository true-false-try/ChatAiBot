package com.bot.chat_ai_bot.service.ai;

import com.bot.chat_ai_bot.dto.AiResponseDto;
import com.bot.chat_ai_bot.dto.prompt.ContextPromptDto;
import com.bot.chat_ai_bot.entity.SessionEntity;
import com.bot.chat_ai_bot.mapper.AiMapper;
import com.bot.chat_ai_bot.mapper.TelegramBotMapper;
import com.bot.chat_ai_bot.service.LanguageService;
import com.bot.chat_ai_bot.service.PromptService;
import com.bot.chat_ai_bot.service.SessionService;
import com.bot.chat_ai_bot.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.math.BigInteger;
import java.util.List;

@Service
@AllArgsConstructor
public class AiService {
    private final List<ChatModel> models;
    private final JpaChatMemory chatMemory;
    private final AiMapper aiMapper;

    public AiResponseDto generateResponse(String chatId, ContextPromptDto contextPromptDto, String userMessage){
        for (ChatModel model : models) {
            try {
                String response = ChatClient.create(model)
                        .prompt()
                        .system(contextPromptDto.getPromptContext())
                        .user(userMessage)
                        .advisors(new MessageChatMemoryAdvisor(chatMemory, chatId, 20))
                        .call()
                        .content();

                if (response!= null && !response.isBlank()) {
                    return aiMapper.toAi(response);
                }
            } catch (Exception ex) {
                System.err.println("Model failed: " + model.getClass().getSimpleName());
                ex.printStackTrace();
            }
        }
        return aiMapper.toAi("I have overload, please try again later...");
    }

}
