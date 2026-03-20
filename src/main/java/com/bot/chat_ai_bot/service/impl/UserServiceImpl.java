package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.dto.UserDto;
import com.bot.chat_ai_bot.entity.SessionEntity;
import com.bot.chat_ai_bot.entity.SessionMessageEntity;
import com.bot.chat_ai_bot.entity.UserEntity;
import com.bot.chat_ai_bot.mapper.SessionMapper;
import com.bot.chat_ai_bot.mapper.SessionMessageMapper;
import com.bot.chat_ai_bot.mapper.TelegramBotMapper;
import com.bot.chat_ai_bot.mapper.UserMapper;
import com.bot.chat_ai_bot.repository.SessionRepository;
import com.bot.chat_ai_bot.repository.UserRepository;
import com.bot.chat_ai_bot.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.math.BigInteger;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final UserMapper userMapper;
    private final SessionMapper sessionMapper;
    private final SessionMessageMapper sessionMessageMapper;
    private final TelegramBotMapper telegramBotMapper;

    @Override
    @Transactional
    public void saveUser(Message message, String response, String language) {

        UserDto userDto = telegramBotMapper.toUserDto(
                message.getFrom(),
                message.getChatId().toString(),
                message.getDate().longValue(),
                language
        );

        UserEntity userEntity = userRepository.findById(userDto.userId())
                .orElse(userMapper.toUserEntity(userDto));

        SessionEntity sessionEntity = sessionRepository.findByUserId(BigInteger.valueOf(Long.parseLong(userDto.chatId())))
                .orElse(sessionMapper.toSessionEntity(userDto, userEntity, language));
        SessionMessageEntity sessionMessageEntity = sessionMessageMapper.mapToSessionEntity(
                sessionEntity,
                message.getText(),
                response);
        sessionMessageEntity.setSession(sessionEntity);
        sessionEntity.getMessages().add(sessionMessageEntity);

        userEntity.getSessions().add(sessionEntity);
        userRepository.save(userEntity);
    }
}

