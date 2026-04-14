package com.bot.chat_ai_bot.service.ai.impl;

import com.bot.chat_ai_bot.dto.broker.MoodTaskDto;
import com.bot.chat_ai_bot.entity.Mood;
import com.bot.chat_ai_bot.service.ai.OllamaAnalysisService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaAnalysisServiceImpl implements OllamaAnalysisService {
    private final ChatModel analyticsModel;

    private final BeanOutputConverter<MoodResponse> outputConverter = new BeanOutputConverter<>(MoodResponse.class);

    @Override
    public Mood analyzeMood(List<MoodTaskDto> batch) {
        String conversationHistory = String.valueOf(batch.stream()
                .map(task ->
                        "USER request: " + task.userRequest() +
                        " " +
                        "AI response: " + task.aiResponse())
                .toList());

        PromptTemplate template = getPromptTemplate();
        Prompt prompt = template.create(Map.of(
                "allowed_moods", List.of(Arrays.stream(
                        Mood.values()).map(value -> value.name()
                        .concat(" "))
                        .toList()),
                "history", conversationHistory,
                "format_instructions", outputConverter.getFormat()
        ));

        try {
            var response = analyticsModel.call(prompt);
            MoodResponse result = outputConverter.convert(response.getResult().getOutput().getContent());
            return result != null ? result.detectedMood() : Mood.UNKNOWN;
        } catch (Exception ex) {
            return Mood.UNKNOWN;
        }
    }

    private static @NonNull PromptTemplate getPromptTemplate() {
        return new PromptTemplate(
                """
                 Analyze the following user messages and determine their overall emotional state (Mood).
                 You MUST choose exactly one value from the following list: {allowed_moods}.
                 User messages to analyze:
                 {history}
                 {format_instructions}
                 """
        );
    }

    public record MoodResponse(Mood detectedMood) {}

}
