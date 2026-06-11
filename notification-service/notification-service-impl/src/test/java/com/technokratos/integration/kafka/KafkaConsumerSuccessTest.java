package com.technokratos.integration.kafka;

import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.integration.BaseIntegrationTest;
import com.technokratos.integration.BaseKafkaIntegrationTest;
import com.technokratos.model.Notification;
import com.technokratos.repository.NotificationRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class KafkaConsumerSuccessTest extends BaseKafkaIntegrationTest {

    @Test
    void consumeUserRegisteredEvent_success() throws Exception {
        UUID expectedUserId = UUID.randomUUID();
        String expectedEmail = "test@gmail.com";

        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                expectedUserId,
                "testUsername",
                expectedEmail
        );

        kafkaTemplate.send("user-registered-event", expectedUserId.toString(), event).get();

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    var allNotifications = repository.findAll();

                    assertFalse(allNotifications.isEmpty(), "Репозиторий MongoDB пуст!");

                    Notification savedNotification = allNotifications.get(0);
                    assertNotNull(savedNotification.getUserId());
                    assertEquals(expectedEmail, savedNotification.getEmail());
                });
    }
}
