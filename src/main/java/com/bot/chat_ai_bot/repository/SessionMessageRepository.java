package com.bot.chat_ai_bot.repository;

import com.bot.chat_ai_bot.entity.SessionMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionMessageRepository extends JpaRepository<SessionMessageEntity, Long> {}
