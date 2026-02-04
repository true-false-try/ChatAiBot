package com.bot.chat_ai_bot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "session")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SessionEntity {
        @Id
        private UUID id;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private UserEntity user;
        private Long createdAt;
        private Long updatedAt;

        @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
        private List<SessionMessageEntity> messages = new ArrayList<>();
}
