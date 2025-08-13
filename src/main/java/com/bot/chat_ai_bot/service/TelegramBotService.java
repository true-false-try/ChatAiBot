package com.bot.chat_ai_bot.service;

import java.util.Map;

public interface TelegramBotService  {
    void update(Map<String,Object> update);
}
