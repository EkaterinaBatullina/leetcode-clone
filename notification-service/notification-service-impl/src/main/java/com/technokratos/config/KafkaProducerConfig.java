package com.technokratos.config;

import com.technokratos.config.property.KafkaProducerProperties;
import com.technokratos.config.property.KafkaCommonProperties;
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
    private final KafkaCommonProperties kafkaProperties;
    private final KafkaProducerProperties kafkaProducerProperties;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> producerConfigs = new HashMap<>();
        producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerProperties.getKeySerializer());
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerProperties.getValueSerializer());
        producerConfigs.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProducerProperties.getClientId());
        return producerConfigs;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
