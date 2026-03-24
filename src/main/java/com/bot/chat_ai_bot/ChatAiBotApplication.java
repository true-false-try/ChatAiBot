package com.bot.chat_ai_bot;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication(exclude = {
        org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration.class
})
@EnableRedisRepositories
@EnableCaching
@EnableRabbit
public class ChatAiBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatAiBotApplication.class, args);
    }

}
