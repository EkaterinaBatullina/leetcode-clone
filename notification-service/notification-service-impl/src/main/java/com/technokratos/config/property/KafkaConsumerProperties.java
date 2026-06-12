package com.technokratos.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public class KafkaConsumerProperties {
    private String groupId;
    private String autoOffsetReset;
    private boolean enableAutoCommit;
    private Class<?> keyDeserializer;
    private Class<?> valueDeserializer;
    private Map<String, String> properties = new HashMap<>();
}
