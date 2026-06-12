package com.technokratos.integration.kafka;

import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.exception.DuplicateEventException;
import com.technokratos.integration.BaseKafkaIntegrationTest;
import com.technokratos.service.NotificationServiceImpl;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Duration;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class KafkaConsumerRetryTest extends BaseKafkaIntegrationTest {
    @SpyBean
    private NotificationServiceImpl service;

    @AfterEach
    void tearDown() {
        reset(service);
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

        kafkaTemplate.send("user-registered-event", userId.toString(), event).get();

        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    verify(service, times(3)).saveUserRegisteredEvent(any(UserRegisteredEvent.class));
                    verify(service, never()).sendWelcomeNotification(any());
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

        kafkaTemplate.send("user-registered-event", userId.toString(), event).get();

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    verify(service, times(1))
                            .saveUserRegisteredEvent(any());

                    verify(service, never())
                            .sendWelcomeNotification(any());
                });
    }
}
