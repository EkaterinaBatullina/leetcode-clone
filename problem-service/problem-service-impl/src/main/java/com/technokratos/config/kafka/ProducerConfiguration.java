package com.technokratos.config.kafka;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class ProducerConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    public Map<String, Object> producerConf() {
        return new HashMap<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConf());
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic runTopic() {
        return new NewTopic("run-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic submitTopic() {
        return new NewTopic("submit-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic runWithWrapperTopic() {
        return new NewTopic("run-with-wrapper-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic submitWithWrapperTopic() {
        return new NewTopic("submit-with-wrapper-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic publishTestcasesTopic() {
        return new NewTopic("publish-testcases-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic publishProblemsTopic() {
        return new NewTopic("publish-problems-topic", 2, (short) 1);
    }
}
