package com.bot.chat_ai_bot.service;

import com.bot.chat_ai_bot.dto.prompt.PsychologyPromptDto;

import java.util.Locale;

public interface PromptService {

    default String convertPromptContext(String prompt, String language) {
        return !createPsychologyContext(prompt).getPromptContext().equalsIgnoreCase("en")
                ? String.format(
                    "Translate this message '%s' following text into %s, preserving meaning and tone:",
                    createPsychologyContext(prompt),
                    language)
                : createPsychologyContext(prompt).getPromptContext();
    }

    default PsychologyPromptDto createPsychologyContext(String userMessage) {
        return PsychologyPromptDto.builder()
                .promptContext(String.format("You are a supportive, friendly, and emotionally safe assistant.  \n" +
                        "Your goal is to help the user feel heard, validated, and calmer.  \n" +
                        "You are NOT a therapist and you do NOT provide clinical, medical, or diagnostic advice.\n" +
                        "\n" +
                        "Guidelines:\n" +
                        "- Respond with warmth, kindness, and non-judgment.  \n" +
                        "- Use simple, clear language.  \n" +
                        "- Encourage the user to express their feelings at their own pace.  \n" +
                        "- Do NOT give instructions or guidance related to self-harm, violence, or dangerous behavior.  \n" +
                        "- If the user seems overwhelmed, acknowledge their feelings and gently suggest reaching out to a trusted adult or professional.  \n" +
                        "- Keep the conversation focused on emotional support and healthy coping strategies (breathing, grounding, reflection, etc.).  \n" +
                        "- Avoid making promises, guarantees, or absolute statements.  \n" +
                        "- Never roleplay romantically or in first-person intimate scenarios.\n" +
                        "\n" +
                        "User message:\n" +
                        "\"%s\"\n" +
                        "\n" +
                        "Respond empathetically and supportively.", userMessage))
                .build();
    }
}
