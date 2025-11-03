package com.bot.chat_ai_bot.config.vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.vault.authentication.UsernamePasswordAuthentication;
import org.springframework.vault.authentication.UsernamePasswordAuthenticationOptions;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_CREDENTIAL_EXCEPTION;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_HOST;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_HOST_PORT_EXCEPTION;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_LOGIN;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_PASSWORD;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_PORT;

@Configuration
public class VaultPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, String> vaultVariables = getVaultCredentialsAndHosAndPort();
        VaultEndpoint vaultEndpoint = VaultEndpoint.create(
                vaultVariables.get(VAULT_HOST),
                Integer.parseInt(vaultVariables.get(VAULT_PORT))
        );

        UsernamePasswordAuthenticationOptions options =
               UsernamePasswordAuthenticationOptions.builder()
                       .username(vaultVariables.get(VAULT_LOGIN))
                       .password(vaultVariables.get(VAULT_PASSWORD))
                       .path("/v1/auth/userpass/login/")
                       .build();
        RestOperations restOperations = new RestTemplate();

        UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication(options, restOperations);

        VaultTemplate vaultTemplate = new VaultTemplate(vaultEndpoint, authentication);
        String secretValue = vaultTemplate.read("secret/data/myapp").getData().get("value").toString();
        System.out.println("Secret from Vault: " + secretValue);



    }

    private Map <String, String> getVaultCredentialsAndHosAndPort() {
        String login = System.getenv(VAULT_LOGIN);
        String password = System.getenv(VAULT_PASSWORD);
        String host = System.getenv(VAULT_HOST);
        String port = System.getenv(VAULT_PORT);

        if (login == null || password == null) {
            throw new IllegalStateException(String.format(VAULT_CREDENTIAL_EXCEPTION, VAULT_LOGIN, VAULT_PASSWORD));
        } else if (host == null || port == null) {
            throw new IllegalStateException(String.format(VAULT_HOST_PORT_EXCEPTION, VAULT_HOST, VAULT_PORT));
        }

        return new HashMap<>(Map.of(
                VAULT_LOGIN, login,
                VAULT_PASSWORD, password,
                VAULT_HOST, host,
                VAULT_PORT, port
        ));
    }
}
