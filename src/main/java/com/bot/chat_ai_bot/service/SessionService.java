package com.bot.chat_ai_bot.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SessionService {
    @Transactional(readOnly = true)
    List<Message> getSessionMessages(Long userId, int lastNumber);
    void clearSession(Long userId);
}
