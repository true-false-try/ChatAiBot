package com.bot.chat_ai_bot.mapper;

import com.bot.chat_ai_bot.entity.SessionEntity;
import com.bot.chat_ai_bot.entity.SessionMessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Instant.class})
public interface SessionMessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(Instant.now().getEpochSecond())")
    @Mapping(target = "session", source = "sessionEntity")
    @Mapping(target = "request", source = "userRequest")
    @Mapping(target = "response", source = "geminiResponse")
    SessionMessageEntity mapToSessionEntity(SessionEntity sessionEntity, String userRequest, String geminiResponse);
}
