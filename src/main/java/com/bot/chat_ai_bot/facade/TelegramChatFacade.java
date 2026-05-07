package com.bot.chat_ai_bot.facade;

import com.bot.chat_ai_bot.dto.AiResponseDto;
import com.bot.chat_ai_bot.dto.broker.MoodTaskDto;
import com.bot.chat_ai_bot.service.SessionService;
import com.bot.chat_ai_bot.service.UserService;
import com.bot.chat_ai_bot.service.ai.AiService;
import com.bot.chat_ai_bot.service.broker.MoodProducer;
import groovy.util.logging.Slf4j;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;


@lombok.extern.slf4j.Slf4j
@Component
@Slf4j
@AllArgsConstructor
public class TelegramChatFacade {
    private final SessionService sessionService;
    private final UserService userService;
    private final AiService aiService;
    private final MoodProducer moodProducer;

    public AiResponseDto processRequest(Message message) {
        String chatId = message.getChatId().toString();
        String lang = sessionService.getOrDetectLanguage(message);
        log.info("Run process chatting, chatId: {}, language: {}", chatId, lang);

        AiResponseDto aiResponse = aiService.generateResponse(
                chatId,
                sessionService.createContext(lang),
                message.getText());

        sendToMoodAnalysis(message, aiResponse);

        userService.saveUser(message, aiResponse, lang);

        return aiResponse;
    }

    private void sendToMoodAnalysis(Message message, AiResponseDto aiResponse) {
        MoodTaskDto task = new MoodTaskDto(
                message.getFrom().getId(),
                message.getText(),
                aiResponse.response(),
                message.getChatId()
        );

        moodProducer.sendMoodRate(task);
    }
}
