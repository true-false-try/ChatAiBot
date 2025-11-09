package com.bot.chat_ai_bot.config.vault.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Map;

public record LoginResponseDto(
        Auth auth
) {
    public record Auth(
            @JsonProperty("client_token")
            String clientToken
    ) {}
}