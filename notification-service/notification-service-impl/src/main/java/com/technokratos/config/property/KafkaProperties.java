package com.technokratos.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private String groupId;
    private String clientId;
    private String keyDeserializer;
    private String keySerializer;
    private String valueDeserializer;
    private String valueSerializer;
    private String defaultType;
    private String trustedPackages;
    private boolean enableAutoCommit;
}
