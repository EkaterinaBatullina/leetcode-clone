package com.technokratos.config;

import com.technokratos.config.property.KafkaConsumerProperties;
import com.technokratos.config.property.KafkaListenerProperties;
import com.technokratos.config.property.KafkaCommonProperties;
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
    private final KafkaCommonProperties kafkaProperties;
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final KafkaListenerProperties kafkaListenerProperties;

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> consumerConfig = new HashMap<>();
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerProperties.getKeyDeserializer());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConsumerProperties.getValueDeserializer());
        if (kafkaConsumerProperties.getProperties() != null) {
            consumerConfig.putAll(kafkaConsumerProperties.getProperties());
        }

        /*
         * Сопоставление значения Kafka-заголовка "__TypeId__"
         * с конкретным Java-классом события.
         *
         * Позволяет использовать единый consumer для разных типов сообщений
         * без передачи полного имени класса в payload.
         */
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getGroupId());
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConsumerProperties.isEnableAutoCommit());
        return consumerConfig;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        DefaultKafkaConsumerFactory<String, Object> factory =
                new DefaultKafkaConsumerFactory<>(consumerConfig());

        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();

        ErrorHandlingDeserializer<Object> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(jsonDeserializer);

        factory.setValueDeserializer(errorHandlingDeserializer);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        factory.setAutoStartup(kafkaListenerProperties.isAutoStartup());

        /*
         * Подтверждение сообщения выполняется вручную.
         *
         * Offset фиксируется только после успешной обработки события,
         * что позволяет повторно получить сообщение при ошибке
         * до момента acknowledge().
         */
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
