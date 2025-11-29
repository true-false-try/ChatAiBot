package com.bot.chat_ai_bot.entity;

import com.bot.chat_ai_bot.utils.JsonMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "user_mood")
public record UserMoodEntity(
        @Id
        @GeneratedValue
        BigInteger id,
        UserEntity user,
        BigInteger userId,
        @Enumerated(EnumType.STRING)
        List<Mood> moods,
        @Convert(converter = JsonMapConverter.class)
        @Column(name = "risk_flag")
        Map<String,Object> riskFlag
) {}
