package com.bot.chat_ai_bot.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.sql.DataSource;

@Configuration
public class PostgresConfig {

    @Bean
    public DataSource dataSource(ConfigurableEnvironment environment) {
        String username = environment.getProperty("postgres.username");
        String password = environment.getProperty("postgres.password");
        String url = environment.getProperty("postgres.url");
        String dbName = environment.getProperty("postgres.db.name");

        if(username == null || password == null || url == null || dbName == null) {
            throw new IllegalStateException("Postgres properties missing from Vault!");
        }

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(fullUrl(url, dbName));
        ds.setUsername(username);
        ds.setPassword(password);

        return ds;
    }

    private String fullUrl(String url, String dbName) {
        return url.concat("/").concat(dbName);
    }

}
