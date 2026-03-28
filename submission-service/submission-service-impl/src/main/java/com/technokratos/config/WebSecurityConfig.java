package com.technokratos.config;

import com.technokratos.filter.WebhookSecretFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {
    private final WebhookSecretFilter webhookSecretFilter;

    public WebSecurityConfig(WebhookSecretFilter webhookSecretFilter) {
        this.webhookSecretFilter = webhookSecretFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/judge0/webhook").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(webhookSecretFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}