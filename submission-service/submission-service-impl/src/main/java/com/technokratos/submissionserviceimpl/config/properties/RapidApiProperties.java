package com.technokratos.submissionserviceimpl.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rapid-api")
public class RapidApiProperties {
    private String apiKey;
    private String apiHost;
    private String callbackUrl;
    private String batchUrl;
    private String singleUrl;
    private int batchSize;
    private String secret;
}