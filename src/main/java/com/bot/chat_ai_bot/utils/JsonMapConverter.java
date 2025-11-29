package com.bot.chat_ai_bot.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class JsonMapConverter implements AttributeConverter<Map<String, Object>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) return "{}";
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JacksonException ex) {
            throw new IllegalArgumentException("Failed to convert Map to JSON", ex);
        }
    }

    @Override
    public Map<String,Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return new HashMap<>();
        try {
            return objectMapper.readValue(dbData, HashMap.class);
        } catch (JacksonException ex) {
            throw new IllegalArgumentException("Failed to convert JSON to Map", ex);
        }
    }
}
