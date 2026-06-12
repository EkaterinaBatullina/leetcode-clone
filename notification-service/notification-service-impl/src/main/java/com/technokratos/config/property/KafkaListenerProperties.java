package com.technokratos.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.kafka.listener")
public class KafkaListenerProperties {
    private boolean autoStartup;
}
