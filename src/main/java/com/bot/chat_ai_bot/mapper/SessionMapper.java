package com.bot.chat_ai_bot.mapper;

import com.bot.chat_ai_bot.dto.UserDto;
import com.bot.chat_ai_bot.entity.SessionEntity;
import com.bot.chat_ai_bot.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.UUID;


@Mapper(componentModel = "spring", imports = {UUID.class, Instant.class})
public interface SessionMapper {

    @Mapping(target = "id", expression = "java(UUID.nameUUIDFromBytes(userDto.chatId().getBytes()))")
    @Mapping(target = "createdAt", expression = "java(Instant.now().getEpochSecond())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now().getEpochSecond())")
    @Mapping(target = "user", source = "userEntity")
    @Mapping(target = "messages", ignore = true)
    SessionEntity toSessionEntity(UserDto userDto, UserEntity userEntity);
}