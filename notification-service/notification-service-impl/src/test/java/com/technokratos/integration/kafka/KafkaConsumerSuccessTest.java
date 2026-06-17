package com.technokratos.integration.kafka;

import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.integration.BaseKafkaIntegrationTest;
import com.technokratos.model.Notification;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConsumerSuccessTest extends BaseKafkaIntegrationTest {

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

        String rawJson = objectMapper.writeValueAsString(event);

        ProducerRecord<String, String> record = new ProducerRecord<>(
                "user-registered-event",
                expectedUserId.toString(),
                rawJson
        );

        record.headers().add(
                "__TypeId__",
                "user_registered".getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        kafkaTemplate.send(record).get();

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
