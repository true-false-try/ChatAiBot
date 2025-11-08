package com.bot.chat_ai_bot.config.vault;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.env.MapPropertySource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.vault.authentication.UsernamePasswordAuthentication;
import org.springframework.vault.authentication.UsernamePasswordAuthenticationOptions;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

import java.util.HashMap;
import java.util.Map;

import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.PROTOCOL_HTTPS;
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
        VaultEndpoint vaultEndpoint = new VaultEndpoint();
        vaultEndpoint.setScheme(PROTOCOL_HTTPS);
        vaultEndpoint.setHost(vaultVariables.get(VAULT_HOST));
        vaultEndpoint.setPort(Integer.parseInt(vaultVariables.get(VAULT_PORT)));

        UsernamePasswordAuthenticationOptions options =
                UsernamePasswordAuthenticationOptions.builder()
                        .username(vaultVariables.get(VAULT_LOGIN))
                        .password(vaultVariables.get(VAULT_PASSWORD))
                        .build();

        RestTemplate restOperations = createRestTemplateTrustAllCerts();

        UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication(options, restOperations);

        VaultTemplate vaultTemplate = new VaultTemplate(vaultEndpoint, authentication);

        VaultKeyValueOperations kvOps = vaultTemplate.opsForKeyValue(
                "ms-telegram-psy-chat-bot",
                VaultKeyValueOperationsSupport.KeyValueBackend.KV_2
        );

        Map<String, Object> data = kvOps.get("test-config").getData();

        if (data != null) {
            System.out.println("✅ Vault secret (test-config): " + data);

            Map<String, Object> vaultProps = new HashMap<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                vaultProps.put(entry.getKey(), entry.getValue());
            }

            environment.getPropertySources().addFirst(
                    new MapPropertySource("vault-secrets", vaultProps)
            );

            System.out.println("🔹 Vault secrets added to Spring Environment");
        } else {
            System.err.println("⚠️ No data found in Vault at path: test-config");
        }

    }

    // For test, disable check TLS certificate
    private RestTemplate createRestTemplateTrustAllCerts() {
        try {
            SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .build();

            HttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

            return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
