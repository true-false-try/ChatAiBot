package com.bot.chat_ai_bot.dto;

import java.math.BigInteger;

public record UserDto (
        BigInteger userId,
        String firstName,
        String lastName,
        String userName,
        Long createdAt,
        String languageCode,
        String chatId
) {}
