package com.technokratos.integration.controller;

import com.technokratos.dto.CustomPageImpl;
import com.technokratos.dto.enums.Status;
import com.technokratos.dto.response.NotificationResponse;
import com.technokratos.integration.BaseIntegrationTest;
import com.technokratos.model.Notification;
import com.technokratos.repository.NotificationRepository;
import com.technokratos.util.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationControllerTest extends BaseIntegrationTest {
    @Autowired
    private TestRestTemplate template;
    @Autowired
    private NotificationRepository repository;
    @Autowired
    private JwtTestUtil jwtTestUtil;

    @BeforeEach
    void setupAll() {
        repository.deleteAll();

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
    void getAllByStatus_whenAdmin_thenReturnOk() {
        String testToken = jwtTestUtil.generateAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(testToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<CustomPageImpl<NotificationResponse>> response = template.exchange(
                "/api/v1/notifications/status/%s".formatted(Status.SAVE),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                }
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
    void getAllByStatus_whenUser_thenReturnForbidden() {
        String testToken = jwtTestUtil.generateUserToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(testToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<CustomPageImpl<NotificationResponse>> response = template.exchange(
                "/api/v1/notifications/status/%s".formatted(Status.SAVE),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                }
        );

        assertNotNull(response);
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(403)));
    }

    @Test
    void getAllByUserId_whenAdmin_thenReturnOk() {
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

        String testToken = jwtTestUtil.generateAdminToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(testToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

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

    @Test
    void getAllByUserId_whenUser_thenReturnForbidden() {
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

        String testToken = jwtTestUtil.generateUserToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(testToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<CustomPageImpl<NotificationResponse>> response = template.exchange(
                "/api/v1/notifications/user/%s".formatted(expectedUserId),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                }
        );

        assertNotNull(response);
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(403)));
    }
}