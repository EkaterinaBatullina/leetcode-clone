package com.technokratos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.config.property.KafkaConsumerProperties;
import com.technokratos.config.property.KafkaListenerProperties;
import com.technokratos.config.property.KafkaCommonProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final KafkaCommonProperties kafkaProperties;
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final KafkaListenerProperties kafkaListenerProperties;

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
               kafkaConsumerProperties.getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                kafkaConsumerProperties.getValueDeserializer());

        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getGroupId());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        if (kafkaConsumerProperties.getProperties() != null) {
            config.putAll(kafkaConsumerProperties.getProperties());
        }

        return config;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            ObjectMapper objectMapper) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setAutoStartup(kafkaListenerProperties.isAutoStartup());

        factory.setRecordMessageConverter(new StringJsonMessageConverter(objectMapper));

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
