package com.technokratos.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oauth2.client.google")
public class GoogleClientProperties {
    private final String clientId;
    private final String clientSecret;
}
