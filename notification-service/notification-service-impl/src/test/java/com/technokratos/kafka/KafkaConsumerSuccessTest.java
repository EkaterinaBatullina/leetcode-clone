package com.technokratos.kafka;

import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.model.Notification;
import com.technokratos.repository.NotificationRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(topics = {"user-registered-event", "user-registered-event-dlt"}, partitions = 1)
@ActiveProfiles(profiles = "test")
@Testcontainers
public class KafkaConsumerSuccessTest {

    @Container
    static MongoDBContainer mongoContainer = new MongoDBContainer(
            DockerImageName.parse("arm64v8/mongo:4.4")
                    .asCompatibleSubstituteFor("mongo"))
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "testuser")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "testpass")
            .withCommand("--bind_ip_all");


    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> String.format(
                "mongodb://%s:%s@%s:%d/testdb?authSource=admin",
                "testuser",
                "testpass",
                mongoContainer.getHost(),
                mongoContainer.getMappedPort(27017)
        ));
    }

    @Autowired
    NotificationRepository repository;

    @Autowired
    KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    @Test
    void consumeUserRegisteredEvent_success() {
        UUID expectedUserId = UUID.randomUUID();
        String expectedEmail = "test@gmail.com";
        Pageable pageable = PageRequest.of(0, 10);
        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                expectedUserId,
                "testUsername",
                expectedEmail
        );

        kafkaTemplate.send("user-registered-event", expectedUserId.toString(), event);

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    Page<Notification> notificationPage = repository.findByUserId(expectedUserId, pageable);
                    assertNotNull( notificationPage);
                    assertFalse(notificationPage.getContent().isEmpty());
                    assertEquals(1, notificationPage.getContent().size());
                    assertEquals(expectedUserId, notificationPage.getContent().get(0).getUserId());
                    assertEquals(expectedEmail, notificationPage.getContent().get(0).getEmail());
                });
    }
}
