package com.bot.chat_ai_bot.repository;

import com.bot.chat_ai_bot.entity.SessionEntity;
import com.bot.chat_ai_bot.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, BigInteger> {

}
