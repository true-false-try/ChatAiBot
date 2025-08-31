package com.bot.chat_ai_bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
public class VaultPostProcessor implements EnvironmentPostProcessor {

    @Value("${VAULT_LOGIN}")
    private String login;

    @Value("${VAULT_PASSWORD}")
    private String password;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (login != null || password != null) {
            throw new IllegalStateException("Vault credentials are not set in environment variables!");
        }
        String url = "http://localhost:8200/v1/auth/userpass/login/" + login;
        Map<String, String> request = Map.of("password", password);
        RestTemplate restTemplate = new RestTemplate();

        Map response = restTemplate.postForObject(url, request, Map.class);
        String clientToken = (String) ((Map) response.get("auth")).get("client_token");

        environment.getSystemProperties().put("VAULT_PASSWORD", clientToken);

    }
}
