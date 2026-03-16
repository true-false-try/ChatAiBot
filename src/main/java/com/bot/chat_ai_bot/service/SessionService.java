package com.bot.chat_ai_bot.service;

import com.bot.chat_ai_bot.entity.SessionEntity;
import org.springframework.ai.chat.messages.Message;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SessionService {
    @Transactional(readOnly = true)
    List<Message> getSessionMessages(Long userId, int lastNumber);
    void clearSession(Long userId);
    Optional<SessionEntity> getSession(Long userId);
}
