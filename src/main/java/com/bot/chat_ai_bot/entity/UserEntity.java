package com.bot.chat_ai_bot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor
public class UserEntity {
    @Id
    private BigInteger id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "created_at", nullable = false)
    private Long createdAt;
    @Column(name = "last_interaction")
    private Long lastInteraction;
    @Column(name = "age_group")
    private String ageGroup;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_mood_id")
    UserMoodEntity userMood;

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = System.currentTimeMillis();
        }
    }
}
