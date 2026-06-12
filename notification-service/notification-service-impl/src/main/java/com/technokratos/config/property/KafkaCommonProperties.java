package com.technokratos.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaCommonProperties {
    private String bootstrapServers;
}
