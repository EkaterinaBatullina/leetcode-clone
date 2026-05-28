package com.technokratos.service;

import com.technokratos.dto.enums.Status;
import com.technokratos.dto.response.NotificationResponse;
import com.technokratos.exception.DuplicateEventException;
import com.technokratos.mapper.NotificationMapper;
import com.technokratos.model.Notification;
import com.technokratos.repository.NotificationRepository;
import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.sender.EmailSender;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    EmailSender sender;
    @Mock
    NotificationRepository repository;
    @Mock
    NotificationMapper mapper;
    @Mock
    NotificationMetricsService notificationMetricsService;
    @InjectMocks
    NotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        try {
            java.lang.reflect.Field field = NotificationServiceImpl.class.getDeclaredField("service");
            field.setAccessible(true);
            field.set(service, notificationMetricsService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        lenient().when(notificationMetricsService.startTimer()).thenReturn(Mockito.mock(Timer.Sample.class));
    }

    @Test
    void saveUserRegisteredEvent_success() {
        UUID expectedEventId = UUID.randomUUID();
        UUID expectedUserId = UUID.randomUUID();
        String expectedEmail = "test@gmail.com";
        String expectedUsername = "testUsername";
        UserRegisteredEvent event = new UserRegisteredEvent(
                expectedEventId,
                expectedUserId,
                expectedUsername,
                expectedEmail
        );

        when(repository.insert(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Notification actualNotification = service.saveUserRegisteredEvent(event);

        assertNotNull(actualNotification);
        assertEquals(expectedEventId.toString(), actualNotification.getId());
        assertEquals(expectedUserId, actualNotification.getUserId());
        assertEquals(Status.PENDING, actualNotification.getStatus());
        assertEquals(expectedEmail, actualNotification.getEmail());

        verify(repository).insert(any(Notification.class));
    }

    @Test
    void saveUserRegisteredEvent_duplicate_shouldThrowException() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "username",
                "test@gmail.com");

        when(repository.insert(any(Notification.class)))
                .thenThrow(new DuplicateKeyException("Duplicate Key"));

        assertThrows(DuplicateEventException.class, () -> {
            service.saveUserRegisteredEvent(event);
        });

        verify(repository).insert(any(Notification.class));
    }

    @Test
    void sendWelcomeNotification_success() {
        UUID expectedUserId = UUID.randomUUID();
        String expectedEmail = "test@gmail.com";
        String expectedUsername = "testUsername";

        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(expectedUserId)
                .email(expectedEmail)
                .status(Status.PENDING)
                .username(expectedUsername)
                .build();

        doNothing().when(sender).send(notification.getEmail(),
                "Welcome!", "Hello %s".formatted(notification.getUsername()));
        when(repository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.sendWelcomeNotification(notification);

        assertNotNull(notification);
        assertEquals(expectedUserId, notification.getUserId());
        assertEquals(expectedUsername, notification.getUsername());
        assertEquals(expectedEmail, notification.getEmail());
        assertEquals(Status.SAVE, notification.getStatus());

        verify(sender).send(notification.getEmail(),
                "Welcome!", "Hello %s".formatted(notification.getUsername()));
        verify(repository).save(any(Notification.class));
        verify(notificationMetricsService).incrementSent();
    }

    @Test
    void sendWelcomeNotification_senderThrowsException() {
        UUID expectedUserId = UUID.randomUUID();
        String expectedEmail = "test@gmail.com";
        String expectedUsername = "testUsername";

        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(expectedUserId)
                .email(expectedEmail)
                .status(Status.PENDING)
                .username(expectedUsername)
                .build();

        doThrow(RuntimeException.class).when(sender).send(notification.getEmail(),
                "Welcome!",  "Hello %s".formatted(notification.getUsername()));
        when(repository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.sendWelcomeNotification(notification);

        assertEquals(expectedUserId, notification.getUserId());
        assertEquals(expectedUsername, notification.getUsername());
        assertEquals(expectedEmail, notification.getEmail());
        assertEquals(Status.FAIL, notification.getStatus());

        verify(sender).send(notification.getEmail(),
                "Welcome!",  "Hello %s".formatted(notification.getUsername()));
        verify(repository).save(any(Notification.class));
        verify(notificationMetricsService).incrementFailed();
    }

    @Test
    void sendWelcomeNotification_repositoryThrowsException() {
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
                .email("test@gmail.com")
                .username("testUsername")
                .status(Status.PENDING)
                .build();

        when(repository.save(any(Notification.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> service.sendWelcomeNotification(notification));

        verify(repository).save(any(Notification.class));
    }

    @Test
    void getAllByStatus_success() {
        String expectedUsername1 = "testUsername1";
        String expectedUsername2 = "testUsername2";
        String expectedEmail1 = "test1@gmail.com";
        String expectedEmail2 = "test2@gmail.com";
        Notification notification1 = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
                .username(expectedUsername1)
                .email(expectedEmail1)
                .status(Status.SAVE)
                .build();
        Notification notification2 = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
                .username(expectedUsername2)
                .email(expectedEmail2)
                .status(Status.SAVE)
                .build();

        List<Notification> notificationList = List.of(
                notification1,
                notification2
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> notificationPage = new PageImpl<>(notificationList, pageable, notificationList.size());

        when(repository.findByStatus(Status.SAVE, pageable)).thenReturn(notificationPage);
        when(mapper.toResponse(notification1)).thenReturn(new NotificationResponse(
                notification1.getUsername(),
                notification1.getEmail(),
                notification1.getStatus(),
                notification1.getCreatedAt()));
        when(mapper.toResponse(notification2)).thenReturn(new NotificationResponse(
                notification2.getUsername(),
                notification2.getEmail(),
                notification2.getStatus(),
                notification2.getCreatedAt()));

        Page<NotificationResponse> notificationResponsePage = service.getAllByStatus(Status.SAVE, pageable);

        assertNotNull(notificationResponsePage);
        assertEquals(2, notificationResponsePage.getContent().size());
        assertEquals(expectedUsername1, notificationResponsePage.getContent().get(0).username());
        assertEquals(expectedUsername2, notificationResponsePage.getContent().get(1).username());
        assertEquals(expectedEmail1, notificationResponsePage.getContent().get(0).email());
        assertEquals(expectedEmail2, notificationResponsePage.getContent().get(1).email());

        verify(repository).findByStatus(Status.SAVE, pageable);
        verify(mapper).toResponse(notification1);
        verify(mapper).toResponse(notification2);
    }


    @Test
    void getAllByStatus_repositoryThrowsException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findByStatus(Status.SAVE, pageable)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> service.getAllByStatus(Status.SAVE, pageable));

        verify(repository).findByStatus(Status.SAVE, pageable);
    }

    @Test
    void getAllByUserId_success() {
        UUID userId = UUID.randomUUID();
        String expectedUsername = "testUsername";
        String expectedEmail = "test1@gmail.com";
        Notification notification1 = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .username(expectedUsername)
                .email(expectedEmail)
                .status(Status.SAVE)
                .build();
        Notification notification2 = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .username(expectedUsername)
                .email(expectedEmail)
                .status(Status.SAVE)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        List<Notification> notificationList = List.of(
                notification1,
                notification2
        );

        Page<Notification> notificationPage = new PageImpl<>(notificationList, pageable, notificationList.size());

        when(repository.findByUserId(userId, pageable)).thenReturn(notificationPage);
        when(mapper.toResponse(notification1)).thenReturn(new NotificationResponse(
                notification1.getUsername(),
                notification1.getEmail(),
                notification1.getStatus(),
                notification1.getCreatedAt()));
        when(mapper.toResponse(notification2)).thenReturn(new NotificationResponse(
                notification2.getUsername(),
                notification2.getEmail(),
                notification2.getStatus(),
                notification2.getCreatedAt()));

        Page<NotificationResponse> notificationResponsePage = service.getAllByUserId(userId, pageable);

        assertNotNull(notificationResponsePage);
        assertEquals(2, notificationResponsePage.getContent().size());
        assertEquals(expectedUsername, notificationResponsePage.getContent().get(0).username());
        assertEquals(expectedUsername, notificationResponsePage.getContent().get(1).username());
        assertEquals(expectedEmail, notificationResponsePage.getContent().get(0).email());
        assertEquals(expectedEmail, notificationResponsePage.getContent().get(1).email());

        verify(repository).findByUserId(userId, pageable);
        verify(mapper).toResponse(notification1);
        verify(mapper).toResponse(notification2);
    }

    @Test
    void getAllByUserId_repositoryThrowsException() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findByUserId(userId, pageable)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> service.getAllByUserId(userId, pageable));

        verify(repository).findByUserId(userId, pageable);
    }
}
