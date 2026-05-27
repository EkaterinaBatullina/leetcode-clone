package com.technokratos.config;

import com.technokratos.config.property.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> consumerConfig = new HashMap<>();
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getKeyDeserializer());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getValueDeserializer());
        consumerConfig.put(JsonDeserializer.TRUSTED_PACKAGES, kafkaProperties.getTrustedPackages());

        consumerConfig.put(JsonDeserializer.TYPE_MAPPINGS, kafkaProperties.getTypeMappings());

        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.isEnableAutoCommit());
        return consumerConfig;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        DefaultKafkaConsumerFactory<String, Object> factory =
                new DefaultKafkaConsumerFactory<>(consumerConfig());

        ErrorHandlingDeserializer<Object> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>());

        factory.setValueDeserializer(errorHandlingDeserializer);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
