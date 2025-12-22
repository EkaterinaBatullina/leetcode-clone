package com.technokratos.config;


import com.technokratos.config.property.KafkaConsumerProperties;
import com.technokratos.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final KafkaConsumerProperties kafkaProperties;

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> consumerConfig = new HashMap<>();
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getKeyDeserializer());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getValueDeserializer());
        consumerConfig.put(JsonDeserializer.TRUSTED_PACKAGES, kafkaProperties.getTrustedPackages());
        consumerConfig.put(JsonDeserializer.VALUE_DEFAULT_TYPE, kafkaProperties.getDefaultType());
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.isEnableAutoCommit());
        return consumerConfig;
    }

    @Bean
    public ConsumerFactory<Long, UserRegisteredEvent> consumerFactory() {
        DefaultKafkaConsumerFactory<Long, UserRegisteredEvent> factory =
                new DefaultKafkaConsumerFactory<>(consumerConfig());
        ErrorHandlingDeserializer<UserRegisteredEvent> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(UserRegisteredEvent.class));
        factory.setValueDeserializer(errorHandlingDeserializer);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, UserRegisteredEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
