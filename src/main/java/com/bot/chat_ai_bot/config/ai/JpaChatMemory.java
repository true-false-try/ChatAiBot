package com.bot.chat_ai_bot.config.ai;

import com.bot.chat_ai_bot.entity.SessionEntity;
import com.bot.chat_ai_bot.entity.SessionMessageEntity;
import com.bot.chat_ai_bot.entity.UserEntity;
import com.bot.chat_ai_bot.repository.SessionMessageRepository;
import com.bot.chat_ai_bot.repository.SessionRepository;
import com.bot.chat_ai_bot.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@AllArgsConstructor
public class JpaChatMemory implements ChatMemory{
    private final SessionRepository sessionRepository;
    private final SessionMessageRepository sessionMessageRepository;
    private final UserRepository userRepository;

    @Override
    public List<Message> get(String conversationId, int lastN) {
        if (conversationId == null || !conversationId.matches("\\d+")) {
            return new ArrayList<>();
        }

        Long userId = Long.parseLong(conversationId);

        return sessionRepository.findFirstByUserIdOrderByUpdatedAtDesc(userId)
                .map(session -> session.getMessages().stream()
                        .flatMap(m -> Stream.of(
                                new UserMessage(m.getRequest()),
                                new AssistantMessage(m.getResponse())
                        ))
                        .map(m -> (Message) m)
                        .collect(Collectors.toList()))
                        .orElseGet(ArrayList::new);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (conversationId == null || !conversationId.matches("\\d+")) {
            return;
        }

        Long userId = Long.parseLong(conversationId);
        SessionEntity session = sessionRepository.findFirstByUserIdOrderByUpdatedAtDesc(userId)
                .orElseGet(() -> createNewSession(userId));

        String userRequest = "";
        String aiResponse = "";

        for (Message m : messages) {
            if (m instanceof UserMessage) userRequest = m.getContent();
            if (m instanceof AssistantMessage) aiResponse = m.getContent();
        }

        if (!userRequest.isEmpty() && !aiResponse.isEmpty()) {
            SessionMessageEntity messageEntity = new SessionMessageEntity();
            messageEntity.setSession(session);
            messageEntity.setRequest(userRequest);
            messageEntity.setResponse(aiResponse);
            messageEntity.setCreatedAt(System.currentTimeMillis());

            sessionMessageRepository.save(messageEntity);

            session.setUpdatedAt(System.currentTimeMillis());
            sessionRepository.save(session);
        }
    }

    @Override
    public void clear(String conversationId) {

    }

    private SessionEntity createNewSession(Long userId) {
        UserEntity user = userRepository.findById(BigInteger.valueOf(userId)).orElseThrow();
        SessionEntity session = new SessionEntity();
        session.setId(UUID.randomUUID());
        session.setUser(user);
        session.setCreatedAt(System.currentTimeMillis());
        session.setUpdatedAt(System.currentTimeMillis());
        return sessionRepository.save(session);
    }
}
