package com.technokratos.kafka;

import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.service.NotificationServiceImpl;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka(topics = {"user-registered-event", "user-registered-event-dlt"}, partitions = 1)
@ActiveProfiles("test")
@Testcontainers
public class KafkaConsumerRetryTest {

    @Autowired
    EmbeddedKafkaBroker embeddedKafka;

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
    KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    @MockBean
    NotificationServiceImpl service;

    @BeforeEach
    void setup() {
        System.setProperty("spring.kafka.bootstrap-servers", embeddedKafka.getBrokersAsString());
    }

    @Test
    void consumeUserRegisteredEvent_serviceThrowsException() {
        UUID expectedUserId = UUID.randomUUID();
        String expectedEmail = "test@gmail.com";
        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                expectedUserId,
                "testUsername",
                expectedEmail
        );

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                "dlt-test-group", "false", embeddedKafka
        );
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.technokratos");
        DefaultKafkaConsumerFactory<String, UserRegisteredEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
                        new JsonDeserializer<>(UserRegisteredEvent.class));

        doThrow(new RuntimeException("service exception")).when(service).saveUserRegisteredEvent(event);

        kafkaTemplate.send("user-registered-event", expectedUserId.toString(), event);

        try (Consumer<String, UserRegisteredEvent> consumer = consumerFactory.createConsumer()) {
            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "user-registered-event-dlt");

            Awaitility
                    .await()
                    .atMost(Duration.ofSeconds(15))
                    .pollInterval(Duration.ofMillis(200))
                    .untilAsserted(() -> {
                            ConsumerRecord<String, UserRegisteredEvent> record = KafkaTestUtils.getSingleRecord(
                                    consumer, "user-registered-event-dlt");

                            assertNotNull(record);
                            assertEquals(expectedUserId, record.value().userId());
                            assertEquals(expectedEmail, record.value().email());

                            verify(service, times(3)).saveUserRegisteredEvent(event);
                    });
        }
    }
}
