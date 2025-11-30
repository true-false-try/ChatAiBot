package com.bot.chat_ai_bot.mapper;

import com.bot.chat_ai_bot.dto.UserDto;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.math.BigInteger;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TelegramBotMapper {
    UserDto toUserDto(BigInteger userId, String firstName, String lastName, String username, String languageCode, String chatId);
}
