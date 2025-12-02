package com.bot.chat_ai_bot.service.impl;

import com.bot.chat_ai_bot.dto.UserDto;
import com.bot.chat_ai_bot.entity.SessionEntity;
import com.bot.chat_ai_bot.entity.SessionMessageEntity;
import com.bot.chat_ai_bot.entity.UserEntity;
import com.bot.chat_ai_bot.mapper.SessionMapper;
import com.bot.chat_ai_bot.mapper.SessionMessageMapper;
import com.bot.chat_ai_bot.mapper.UserMapper;
import com.bot.chat_ai_bot.repository.UserRepository;
import com.bot.chat_ai_bot.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionMapper sessionMapper;
    private final SessionMessageMapper sessionMessageMapper;

    @Override
    @Transactional
    public void saveUser(UserDto userDto, String request, String response){
        UserEntity userEntity = userRepository.findById(userDto.userId())
                .orElse(userMapper.toUserEntity(userDto));

         SessionEntity sessionEntity = sessionMapper.toSessionEntity(userDto, userEntity);
         SessionMessageEntity sessionMessageEntity = sessionMessageMapper.mapToSessionEntity(sessionEntity, request, response);

        sessionEntity.getMessages().add(sessionMessageEntity);
        userEntity.getSessions().add(sessionEntity);

        userRepository.save(userEntity);

    }
}
