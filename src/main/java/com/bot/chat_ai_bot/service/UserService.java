package com.bot.chat_ai_bot.service;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserService {
    void saveUser(Message message, String response, String profileLanguage);
}
