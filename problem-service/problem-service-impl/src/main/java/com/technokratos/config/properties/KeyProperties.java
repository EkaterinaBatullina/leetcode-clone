package com.technokratos.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
public class KeyProperties {
    private final String publicKey;
}
