package com.bot.chat_ai_bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Entity
@Table(name = "mood_history")
@NoArgsConstructor
@AllArgsConstructor
public class MoodHistoryEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        BigInteger id;

        @ManyToOne
        @JoinColumn(name = "user_id")
        UserEntity user;

        @Enumerated(EnumType.STRING)
        Mood mood;

        @Column(name = "triggered_at")
        Long triggeredAt;
}
