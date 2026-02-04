package com.bot.chat_ai_bot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@EqualsAndHashCode
@Table(name = "main_user")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String userName;

    private Long createdAt;
    private Long lastInteraction;

    private String ageGroup;

    @Enumerated(EnumType.STRING)
    private Mood currentMood;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SessionEntity> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MoodHistoryEntity> moodHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RiskFlagEntity> riskFlags = new ArrayList<>();
}
