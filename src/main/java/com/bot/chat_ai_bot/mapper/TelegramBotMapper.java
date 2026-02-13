package com.bot.chat_ai_bot.mapper;

import com.bot.chat_ai_bot.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.math.BigInteger;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TelegramBotMapper {
    UserDto toUserDto(BigInteger userId, String firstName, String lastName, String userName, Long createdAt, String languageCode, String chatId);
}
