package com.bot.chat_ai_bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "system_prompt")
@NoArgsConstructor
@AllArgsConstructor
public class SystemPromptEntity {
    @Id
    @Column(length = 2)
    String id;

    @Column(columnDefinition = "TEXT")
    String prompt;
}
