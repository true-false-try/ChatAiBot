package com.bot.chat_ai_bot.service.telegram_bot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramMessageService {

    @Value("${sticker.id}")
    private String stickerId;


    public SendSticker createStickerMessage(Message message) {
        return new SendSticker(message.getChatId().toString(), new InputFile(stickerId));
    }

    public DeleteMessage removeStickerMessage(Message message) {
        return new DeleteMessage(message.getChatId().toString(), message.getMessageId());
    }

    public SendMessage createTextMessage(String chatId, String text) {
        return new SendMessage(chatId, text);
    }
}
