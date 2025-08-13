package com.bot.chat_ai_bot.controller;

import com.bot.chat_ai_bot.service.TelegramBotService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/telegram")
@AllArgsConstructor
public class TelegramController {
    private final TelegramBotService telegramBotService;

    @PostMapping("update")
    public ResponseEntity<Void> update(@RequestBody Map<String, Object> update) {
        telegramBotService.update(update);
        return ResponseEntity.ok().build();
    }
}
