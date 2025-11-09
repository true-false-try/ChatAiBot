package com.bot.chat_ai_bot.config.vault;
import com.bot.chat_ai_bot.config.vault.dto.LogInRequestDto;
import com.bot.chat_ai_bot.config.vault.dto.LoginResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.core.env.MapPropertySource;
import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

import java.util.HashMap;
import java.util.Map;

import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.PROTOCOL_HTTPS;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.USERPASS_SUFFIX;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_CREDENTIAL_EXCEPTION;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_HOST;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_HOST_PORT_EXCEPTION;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_LOGIN;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_PASSWORD;
import static com.bot.chat_ai_bot.config.vault.constants.VaultConstants.VAULT_PORT;

@Slf4j
@Configuration
public class VaultPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, String> vaultVariables = getVaultCredentialsAndHosAndPort();

        String host = vaultVariables.get(VAULT_HOST);
        String port = vaultVariables.get(VAULT_PORT);
        String login = vaultVariables.get(VAULT_LOGIN);
        String password = vaultVariables.get(VAULT_PASSWORD);

        VaultEndpoint vaultEndpoint = new VaultEndpoint();
        vaultEndpoint.setScheme(PROTOCOL_HTTPS);
        vaultEndpoint.setHost(host);
        vaultEndpoint.setPort(Integer.parseInt(port));

        RestTemplate restTemplate = createRestTemplateTrustAllCerts();

        String loginUrl = vaultEndpoint.createUriString(String.format(USERPASS_SUFFIX, login));
        LogInRequestDto logInRequestDto = new LogInRequestDto(password);

        try {
            ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(loginUrl, logInRequestDto, LoginResponseDto.class);
            log.debug("✅ Login response: {}", response.getBody());
            if (response.getStatusCode().is2xxSuccessful()) {

                String clientToken = response.getBody().auth().clientToken();

                Map<String, Object> secretMap = getKv2SecretWithToken(restTemplate, vaultEndpoint, clientToken,
                        "ms-telegram-psy-chat-bot", "test-config");

                if (secretMap != null && !secretMap.isEmpty()) {
                    System.out.println("✅ secretMap = " + secretMap);
                    environment.getPropertySources().addFirst(new MapPropertySource("vault-secrets", secretMap));
                } else {
                    System.err.println("⚠️ secret is empty or not found");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ Exception login for Vault: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getKv2SecretWithToken(RestTemplate restTemplate,
                                                      VaultEndpoint vaultEndpoint,
                                                      String token,
                                                      String mount,
                                                      String key) {

        String url = vaultEndpoint.createUriString(mount + "/data/" + key);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("X-Vault-Token", token);
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);

        ResponseEntity<Map> resp = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, Map.class);

        if (resp.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = resp.getBody();
            if (body == null) return null;

            Object dataObj = body.get("data");
            if (!(dataObj instanceof Map)) return null;

            Map<String, Object> dataMap = (Map<String, Object>) dataObj;
            Object inner = dataMap.get("data");
            if (!(inner instanceof Map)) return null;

            return (Map<String, Object>) inner;
        } else if (resp.getStatusCode().value() == 404) {
            throw new RuntimeException("Secret not found: " + mount + "/" + key);
        } else if (resp.getStatusCode().value() == 403) {
            throw new RuntimeException("Forbidden: token has no access to " + mount + "/" + key);
        } else {
            throw new RuntimeException("Vault returned " + resp.getStatusCode() + " for " + url);
        }
    }

    // For test, disable check TLS certificate
    private RestTemplate createRestTemplateTrustAllCerts() {
        try {
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(new TrustAllStrategy())
                    .build();

            var sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext)
                    .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

            var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();

            return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        } catch (Exception e) {
            throw new RuntimeException("❌ Exception when creating RestTemplate with custom certificate", e);
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
