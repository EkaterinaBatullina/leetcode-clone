package com.technokratos.submissionserviceimpl.config.kafka;

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

    final KafkaProperties kafkaProperties;

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
    public NewTopic runResponseTopic() {
        return new NewTopic("run-response-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic submitResponseTopic() {
        return new NewTopic("submit-response-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic runWithWrapperResponseTopic() {
        return new NewTopic("run-with-wrapper-response-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic submitWithWrapperResponseTopic() {
        return new NewTopic("submit-with-wrapper-response-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic problemsPublishedResponseTopic() {
        return new NewTopic("problems-published-response-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic publishProblemsFailedTopic(){
        return new NewTopic("publish-problems-failed-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic publishTestcasesResponseTopic(){
        return new NewTopic("publish-testcases-response-topic", 2, (short) 1);
    }
}
