package com.bot.chat_ai_bot.config.vault.constants;

import java.util.stream.Stream;

public class VaultConstants {

    public static final String PROTOCOL_HTTPS = "https";

    public static final String MOUNT = "ms-telegram-psy-chat-bot";
    public static final String SECRET_CONFIG = "test-config";

    public static final String URL_USERPASS_SUFFIX = "auth/userpass/login/%s";

    public static final String HEADER_VAULT_TOKEN = "X-Vault-Token";
    public static final String URL_CONFIG_SUFFIX = MOUNT + "/data/" + SECRET_CONFIG;

    public static final String VAULT_SECRETS = "vault-secrets";

    //Credentials
    public static final String VAULT_HOST = "VAULT_HOST";
    public static final String VAULT_PORT = "VAULT_PORT";
    public static final String VAULT_LOGIN = "VAULT_LOGIN";
    public static final String VAULT_PASSWORD = "VAULT_PASSWORD";

    //Exception
    public static final String VAULT_CREDENTIAL_EXCEPTION = "Vault credentials %s, %s, are not set in environment variables!";
    public static final String VAULT_HOST_PORT_EXCEPTION = "Vault host or port %s, %s, are not set in environment variables!";

}
