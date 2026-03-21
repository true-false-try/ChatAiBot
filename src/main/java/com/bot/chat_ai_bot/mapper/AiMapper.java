package com.bot.chat_ai_bot.mapper;

import com.bot.chat_ai_bot.dto.AiResponseDto;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, Instant.class})
public interface AiMapper {
    AiResponseDto toAi(String response);
}