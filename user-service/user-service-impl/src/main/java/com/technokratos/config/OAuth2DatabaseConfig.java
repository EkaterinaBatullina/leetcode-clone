package com.technokratos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import javax.sql.DataSource;

@Configuration
public class OAuth2DatabaseConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(DataSource dataSource) {
        JdbcOperations jdbcOperations = new JdbcTemplate(dataSource);
        return new JdbcRegisteredClientRepository(jdbcOperations);
    }

    @Bean
    public JdbcOAuth2AuthorizationService oauth2AuthorizationService(
            DataSource dataSource,
            RegisteredClientRepository registeredClientRepository) {
        JdbcOperations jdbcOperations = new JdbcTemplate(dataSource);
        return new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);
    }
}