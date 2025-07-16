package com.bot.chataibot.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotenvPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Dotenv dotenv = Dotenv.configure()
                // should be all path for locally running
                .directory("ChatAiBot")
                .filename(".env")
                .load();
        Map<String, Object> envMap = new HashMap<>();
        dotenv.entries().forEach(entry -> {
            envMap.put(entry.getKey(), entry.getValue());
            System.setProperty(entry.getKey(), entry.getValue());
        });
        environment.getPropertySources().addLast(new MapPropertySource("dotenvProperties", envMap));

        /**
         * Used only for debug
         * envMap.forEach((k, v) -> System.out.println("Key: " + k + " Value: " + v));
         * */
    }
}
