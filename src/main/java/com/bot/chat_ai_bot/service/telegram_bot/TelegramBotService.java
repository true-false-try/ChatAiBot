package com.bot.chat_ai_bot.service.telegram_bot;

import com.bot.chat_ai_bot.dto.prompt.ContextPromptDto;
import com.bot.chat_ai_bot.entity.SessionEntity;
import com.bot.chat_ai_bot.mapper.TelegramBotMapper;
import com.bot.chat_ai_bot.service.LanguageService;
import com.bot.chat_ai_bot.service.PromptService;
import com.bot.chat_ai_bot.service.SessionService;
import com.bot.chat_ai_bot.service.UserService;
import com.bot.chat_ai_bot.service.ai.AiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.name}")
    private String botName;

    @Value("${sticker.id}")
    private String stickerId;

    private final AiService aiService;
    private final UserService userService;
    private final SessionService sessionService;
    private final LanguageService languageService;
    private final CommandService commandService;
    private final TelegramBotMapper telegramBotMapper;
    private final PromptService promptService;

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            try {
                Message inMessage = update.getMessage();
                String userChatId = inMessage.getChatId().toString();

                String userMessage = inMessage.getText();

                if (commandService.handle(update, this)) {return;}

                String sessionLanguage = sessionService.getSession(Long.valueOf(userChatId))
                        .map(SessionEntity::getLanguage)
                        .orElseGet(() -> languageService.getLanguageFromMessage(userMessage));

                Message stickerMsg = execute(new SendSticker(userChatId, new InputFile(stickerId)));

                ContextPromptDto context = promptService.createPsychologyContext(sessionLanguage);

                String aiResponse = aiService.generateResponse(
                        userChatId,
                        context,
                        userMessage
                );

                execute(new SendMessage(userChatId, aiResponse));

                execute(new DeleteMessage(userChatId, stickerMsg.getMessageId()));

                saveUserData(inMessage, userMessage, aiResponse, sessionLanguage);

            } catch (TelegramApiException ex) {
                log.error("Telegram error", ex);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void saveUserData(Message inMessage, String request, String response,String sessionLanguage) {
        userService.saveUser(
                telegramBotMapper.toUserDto(
                        BigInteger.valueOf(inMessage.getFrom().getId()),
                        inMessage.getFrom().getFirstName(),
                        inMessage.getFrom().getLastName(),
                        inMessage.getFrom().getUserName(),
                        Long.valueOf(inMessage.getDate()),
                        inMessage.getFrom().getLanguageCode(),
                        inMessage.getChatId().toString()
                ),
                request, response, sessionLanguage
        );
    }
}
