package com.bot.chat_ai_bot.service;

import com.bot.chat_ai_bot.dto.AiResponseDto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserService {
    void saveUser(Message message, AiResponseDto response, String profileLanguage);
}
