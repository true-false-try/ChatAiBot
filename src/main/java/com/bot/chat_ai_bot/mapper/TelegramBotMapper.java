package com.bot.chat_ai_bot.mapper;

import com.bot.chat_ai_bot.dto.UserDto;
import com.bot.chat_ai_bot.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.telegram.telegrambots.meta.api.objects.User;

import java.math.BigInteger;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TelegramBotMapper {

    @Mapping(target = "userId", source = "tgUser.id")
    @Mapping(target = "firstName", source = "tgUser.firstName")
    @Mapping(target = "lastName", source = "tgUser.lastName")
    @Mapping(target = "userName", source = "tgUser.userName")
    @Mapping(target = "profileLanguage", source = "language")
    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "createdAt", source = "date")
    UserDto toUserDto(User tgUser, String chatId, Long date, String language);
}
