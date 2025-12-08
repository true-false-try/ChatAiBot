package com.bot.chat_ai_bot.dto.prompt;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PsychologyPromptDto {
    private String promptContext;
}
