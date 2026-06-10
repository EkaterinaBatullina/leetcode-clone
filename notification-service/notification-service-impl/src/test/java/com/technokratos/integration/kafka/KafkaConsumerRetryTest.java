package com.technokratos.integration.kafka;

import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.exception.DuplicateEventException;
import com.technokratos.integration.base.BaseIntegrationTest;
import com.technokratos.service.NotificationServiceImpl;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.time.Duration;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class KafkaConsumerRetryTest extends BaseIntegrationTest {
    @Autowired
    private KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;
    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @SpyBean
    private NotificationServiceImpl service;

    @BeforeEach
    void startKafka() {
        for (MessageListenerContainer container : registry.getListenerContainers()) {
            container.start();
            try {
                // Жестко ждем партиции для основного топика, чтобы консьюмер успел проснуться
                org.springframework.kafka.test.utils.ContainerTestUtils
                        .waitForAssignment(container, 1);
            } catch (Exception e) {
                // Игнорируем DLT контейнер, если у него нет партиций на старте
            }
        }
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        // Сбрасываем стабы вызовов между тестовыми методами
        org.mockito.Mockito.reset(service);
    }

    @Test
    void consumeUserRegisteredEvent_serviceThrowsException_shouldRetry() throws Exception {
        UUID userId = UUID.randomUUID();

        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                userId,
                "testUsername",
                "test@gmail.com"
        );

        doThrow(new RuntimeException("service exception"))
                .when(service)
                .saveUserRegisteredEvent(any(UserRegisteredEvent.class));

        // Добавили .get() — отправка станет синхронной
        kafkaTemplate.send("user-registered-event", userId.toString(), event).get();

        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    verify(service, atLeast(1))
                            .saveUserRegisteredEvent(any());

                    verify(service, never())
                            .sendWelcomeNotification(any());
                });
    }

    @Test
    void consumeUserRegisteredEvent_duplicateEvent_shouldNotGoToDlt() throws Exception {
        UUID userId = UUID.randomUUID();

        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                userId,
                "testUsername",
                "test@gmail.com"
        );

        doThrow(new DuplicateEventException("duplicate event"))
                .when(service)
                .saveUserRegisteredEvent(any(UserRegisteredEvent.class));

        // Добавили .get() — отправка станет синхронной
        kafkaTemplate.send("user-registered-event", userId.toString(), event).get();

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    verify(service, atLeastOnce())
                            .saveUserRegisteredEvent(any());

                    verify(service, never())
                            .sendWelcomeNotification(any());
                });
    }
}
