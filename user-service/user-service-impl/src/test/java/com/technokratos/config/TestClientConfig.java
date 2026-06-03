package com.technokratos.config;

import com.technokratos.config.property.OAuth2ClientProperties;
import com.technokratos.client.TestAuthClient;
import com.technokratos.client.TestUserClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.web.client.TestRestTemplate;

@TestConfiguration
public class TestClientConfig {

    @Bean
    public TestAuthClient testAuthClient(TestRestTemplate restTemplate, OAuth2ClientProperties properties) {
        return new TestAuthClient(restTemplate, properties);
    }

    @Bean
    public TestUserClient testUserClient(TestRestTemplate restTemplate) {
        return new TestUserClient(restTemplate);
    }
}