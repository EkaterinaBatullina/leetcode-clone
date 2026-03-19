package com.technokratos.submissionserviceimpl.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "judge0")
public class Judge0Properties {
    private String url;
    private String batchUrl;
    private String singleUrl;
    private String tokensUrl;
    private String callbackUrl;
    private int batchSize;
    private String secret;
}