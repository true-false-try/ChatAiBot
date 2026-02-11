package com.bot.chat_ai_bot.repository;

import com.bot.chat_ai_bot.entity.SystemPromptEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemPromptRepository extends CrudRepository<SystemPromptEntity, String> {}