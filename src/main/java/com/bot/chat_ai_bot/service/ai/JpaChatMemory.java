package com.bot.chat_ai_bot.service.ai;

import com.bot.chat_ai_bot.entity.SessionMessageEntity;
import com.bot.chat_ai_bot.repository.SessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class JpaChatMemory implements ChatMemory{
    private final SessionRepository sessionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Message> get(String conversationId, int lastN) {
        if (conversationId == null || !conversationId.matches("\\d+")) {
            return new ArrayList<>();
        }

        Long userId = Long.parseLong(conversationId);

        return sessionRepository.findFirstByUserIdOrderByUpdatedAtDesc(userId)
                .map(session -> {
                    List<SessionMessageEntity> allMessages = session.getMessages();
                    int size = allMessages.size();
                    int fromIndex = Math.max(0, size - lastN);

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
    public void add(String conversationId, List<Message> messages) {}

    @Override
    public void clear(String conversationId) {

    }

}
