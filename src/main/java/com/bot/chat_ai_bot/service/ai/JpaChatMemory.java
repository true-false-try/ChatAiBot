package com.bot.chat_ai_bot.service.ai;

import com.bot.chat_ai_bot.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaChatMemory implements ChatMemory {
    private final SessionService sessionService;

    @Override
    public List<Message> get(String conversationId, int lastN) {
        if (conversationId == null || !conversationId.matches("\\d+")) {
            return new ArrayList<>();
        }

        Long userId = Long.parseLong(conversationId);

        return sessionService.getSessionMessages(userId, lastN);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {}

    @Override
    public void clear(String conversationId) {
        if (conversationId == null || !conversationId.matches("\\d+")) {
            return;
        }
        Long userId = Long.parseLong(conversationId);
        sessionService.clearSession(userId);
    }
}
