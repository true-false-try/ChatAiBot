package com.bot.chat_ai_bot.service;

import com.bot.chat_ai_bot.dto.UserDto;

import javax.naming.NotContextException;

public interface UserService {
    void saveUser(UserDto userDto, String request, String response);
}
