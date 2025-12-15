package com.technokratos.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.testcontainers.shaded.com.google.common.net.HttpHeaders.CONTENT_TYPE;

@TestConfiguration
public class TestRestTemplateConfig {

    @Bean
    public RestTemplateBuilder getRestTemplateBuilder() {
        return new RestTemplateBuilder()
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    }
}