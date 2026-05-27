package com.technokratos.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProducerProperties {
    private String bootstrapServers;
    private String clientId;
    private String keySerializer;
    private String valueSerializer;
    private String userRegisteredTopic;
    private String userRegisteredToken;
}
