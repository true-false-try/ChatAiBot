package com.bot.chat_ai_bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "session_message")
@NoArgsConstructor
@AllArgsConstructor
public class SessionMessageEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private BigInteger id;

        @ManyToOne
        @JoinColumn(name = "session_id")
        private SessionEntity session;

        @Column(columnDefinition = "LONGTEXT")
        private String request;
        @Column(columnDefinition = "LONGTEXT")
        private String response;
        private String language;
        private Long createdAt;
}
