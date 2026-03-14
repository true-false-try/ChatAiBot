package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.entity.SessionMessageEntity;
import com.bot.chat_ai_bot.repository.SessionRepository;
import com.bot.chat_ai_bot.service.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    @Override
    public List<Message> getSessionMessages(Long userId, int lastNumber) {
        return sessionRepository.findFirstByUserIdOrderByUpdatedAtDesc(userId)
                .map(session -> {
                    List<SessionMessageEntity> allMessages = session.getMessages();
                    int size = allMessages.size();
                    int fromIndex = Math.max(0, size - lastNumber);

                    return allMessages.subList(fromIndex, size).stream()
                            .flatMap(m -> Stream.of(
                                    new UserMessage(m.getRequest()),
                                    new AssistantMessage(m.getResponse())
                            ))
                            .map(m -> (Message) m)
                            .collect(Collectors.toList());
                })
                .orElseGet(ArrayList::new);
    }

    @Override
    public void clearSession(Long userId) {
        sessionRepository.deleteByUserId(userId);
    }
}
