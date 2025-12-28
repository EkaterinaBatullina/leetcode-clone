package com.technokratos.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private String groupId;
    private String clientId;
    private Class<?> keySerializer;
    private Class<?> valueSerializer;
    private Class<?> keyDeserializer;
    private Class<?> valueDeserializer;
    private String defaultType;
    private String trustedPackages;
    private boolean enableAutoCommit;
}
