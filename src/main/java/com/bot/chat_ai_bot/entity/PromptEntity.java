package com.bot.chat_ai_bot.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash("prompt")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromptEntity implements Serializable {
    @Id
    String id;

    String content;
    String role;
    String description;
    String language;
}
