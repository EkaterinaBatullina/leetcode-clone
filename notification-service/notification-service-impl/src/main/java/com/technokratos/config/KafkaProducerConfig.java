package com.technokratos.config;

import com.technokratos.config.property.KafkaProperties;
import com.technokratos.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {
    private final KafkaProperties properties;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> producerConfigs = new HashMap<>();
        producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, properties.getKeySerializer());
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, properties.getValueSerializer());
        producerConfigs.put(ProducerConfig.CLIENT_ID_CONFIG, properties.getClientId());
        return producerConfigs;
    }

    @Bean
    public ProducerFactory<String, UserRegisteredEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
