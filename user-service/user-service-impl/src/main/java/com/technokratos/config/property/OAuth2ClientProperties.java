package com.technokratos.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oauth2.client.my-app")
public class OAuth2ClientProperties {
    private String clientId;
    private String clientSecret;
}