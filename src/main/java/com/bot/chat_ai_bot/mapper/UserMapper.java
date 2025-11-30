package com.bot.chat_ai_bot.mapper;

import com.bot.chat_ai_bot.dto.UserDto;
import com.bot.chat_ai_bot.entity.Mood;
import com.bot.chat_ai_bot.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;


@Mapper(componentModel = "spring", imports = {Mood.class, Instant.class})
public interface UserMapper {

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "createdAt", expression = "java(Instant.now().getEpochSecond())")
    @Mapping(target = "lastInteraction", expression = "java(Instant.now().getEpochSecond())")
    @Mapping(target = "currentMood", expression = "java(Mood.UNKNOWN)")
    @Mapping(target = "ageGroup", ignore = true)
    @Mapping(target = "sessions", ignore = true)
    @Mapping(target = "moodHistories", ignore = true)
    @Mapping(target = "riskFlags", ignore = true)
    UserEntity toUserEntity(UserDto userEntity);
}
