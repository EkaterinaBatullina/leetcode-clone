package com.technokratos.controller;

import com.technokratos.config.TestRestTemplateConfig;
import com.technokratos.dto.CustomPageImpl;
import com.technokratos.dto.enums.Status;
import com.technokratos.dto.response.NotificationResponse;
import com.technokratos.model.Notification;
import com.technokratos.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestRestTemplateConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@EmbeddedKafka
public class NotificationControllerTest {

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
    EmbeddedKafkaBroker embeddedKafka;

    @BeforeEach
    void setup() {
        System.setProperty("spring.kafka.bootstrap-servers", embeddedKafka.getBrokersAsString());
    }

    @Autowired
    TestRestTemplate template;

    @Autowired
    NotificationRepository repository;

    @BeforeEach
    void setupAll() {
        Notification notification1 = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
                .username("testUsername1")
                .email("test1@gmail.com")
                .status(Status.SAVE)
                .build();
        Notification notification2 = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
                .username("testUsername2")
                .email("test2@gmail.com")
                .status(Status.SAVE)
                .build();
        Notification notification3 = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
                .username("testUsername3")
                .email("test3@gmail.com")
                .status(Status.PENDING)
                .build();
        repository.save(notification1);
        repository.save(notification2);
        repository.save(notification3);
    }

    @Test
    void getAllByStatus_success() {
        HttpEntity<String> request = new HttpEntity<>(Status.SAVE.name());
        ResponseEntity<CustomPageImpl<NotificationResponse>> response = template.exchange(
                "/api/v1/notifications/status/%s".formatted(Status.SAVE),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getContent().isEmpty());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals(Status.SAVE, response.getBody().getContent().get(0).status());
        assertEquals(Status.SAVE, response.getBody().getContent().get(1).status());
    }

    @Test
    void getAllByUserId_success() {
        UUID expectedUserId = UUID.randomUUID();
        String expectedUsername = "testUsername";
        String expectedEmail = "test@gmail.com";
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(expectedUserId)
                .username(expectedUsername)
                .email(expectedEmail)
                .status(Status.SAVE)
                .build();
        repository.save(notification);

        HttpEntity<String> request = new HttpEntity<>(expectedUserId.toString());
        ResponseEntity<CustomPageImpl<NotificationResponse>> response = template.exchange(
                "/api/v1/notifications/user/%s".formatted(expectedUserId),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        assertNotNull(response);
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getContent().isEmpty());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(expectedUsername, response.getBody().getContent().get(0).username());
        assertEquals(expectedEmail, response.getBody().getContent().get(0).email());
    }
}
