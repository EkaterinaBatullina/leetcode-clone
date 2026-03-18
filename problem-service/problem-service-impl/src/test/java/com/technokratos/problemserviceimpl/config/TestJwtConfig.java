package com.technokratos.problemserviceimpl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.List;

@Configuration
@Profile("test")
public class TestJwtConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            if (token == null || token.isEmpty()) {
                throw new JwtException("Invalid token");
            }
            
            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("sub", "admin-service")
                    .claim("iss", "http://localhost:9000")
                    .claim("authorities", List.of("ADMIN"))
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .build();
        };
    }
}