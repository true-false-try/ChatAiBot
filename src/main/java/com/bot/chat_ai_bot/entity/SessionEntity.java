package com.bot.chat_ai_bot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "session")
@NoArgsConstructor
@AllArgsConstructor
public class SessionEntity {
        @Id
        UUID id;

        @ManyToOne
        @JoinColumn(name = "user_id")
        UserEntity user;
        Long createdAt;
        Long updatedAt;

        @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
        List<SessionMessageEntity> messages;
}
