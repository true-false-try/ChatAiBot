package com.bot.chat_ai_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
@EnableCaching
public class ChatAiBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatAiBotApplication.class, args);
    }

}
