package com.technokratos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.utils.ContainerTestUtils;

public abstract class BaseKafkaIntegrationTest extends BaseIntegrationTest {
    @Autowired
    protected NotificationRepository repository;
    @Autowired
    protected KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @BeforeEach
    void waitForKafkaListeners() {
        repository.deleteAll();

        for (MessageListenerContainer container : registry.getListenerContainers()) {
            container.start();
            try {
                ContainerTestUtils.waitForAssignment(container, 1);
            } catch (Exception e) {
                throw new RuntimeException("Kafka listener не получил assignment!", e);
            }
        }
    }
}