package com.technokratos.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rsa")
public class KeyProperties {
    private String privateKey;
    private String publicKey;
}