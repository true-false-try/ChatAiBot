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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.math.BigInteger;


@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramMessageService {

    @Value("${sticker.id}")
    private String stickerId;

    private final SessionService sessionService;
    private final LanguageService languageService;
    private final AiService aiService;
    private final UserService userService;
    private final TelegramBotMapper telegramBotMapper;
    private final PromptService promptService;


    public SendMessage processAiResponse(Message message) {
        String userChatId = message.getChatId().toString();
        String userMessage = message.getText();

        String sessionLanguage = getOrDetectLanguage(message);
        ContextPromptDto context = promptService.createPsychologyContext(sessionLanguage);

        String aiResponse = aiService.generateResponse(userChatId, context, userMessage);
        saveUserData(message, userMessage, aiResponse, sessionLanguage);

        return new SendMessage(userChatId, aiResponse);
    }

    public SendSticker createStickerMessage(Message message) {
        return new SendSticker(message.getChatId().toString(), new InputFile(stickerId));
    }

    public DeleteMessage removeStickerMessage(Message message) {
        return new DeleteMessage(message.getChatId().toString(), message.getMessageId());
    }

    private String getOrDetectLanguage(Message message) {
        return sessionService.getSession(message.getChatId())
                .map(SessionEntity::getLanguage)
                .orElseGet(() -> languageService.getLanguageFromMessage(message.getText()));
    }

    private void saveUserData(Message message, String request, String response, String sessionLanguage) {
        userService.saveUser(
                telegramBotMapper.toUserDto(
                        BigInteger.valueOf(message.getFrom().getId()),
                        message.getFrom().getFirstName(),
                        message.getFrom().getLastName(),
                        message.getFrom().getUserName(),
                        Long.valueOf(message.getDate()),
                        message.getFrom().getLanguageCode(),
                        message.getChatId().toString()
                ),
                request, response, sessionLanguage
        );
    }

}
