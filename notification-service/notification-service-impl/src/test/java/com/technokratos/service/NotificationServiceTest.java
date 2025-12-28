package com.technokratos.service;

import com.technokratos.dto.enums.Status;
import com.technokratos.dto.response.NotificationResponse;
import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.mapper.NotificationMapper;
import com.technokratos.model.Notification;
import com.technokratos.repository.NotificationRepository;
import com.technokratos.sender.EmailSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    @InjectMocks
    NotificationServiceImpl service;

    @Test
    void saveUserRegisteredEvent_success() {
        UUID userId = UUID.randomUUID();
        String email = "test@gmail.com";
        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                userId,
                "testUsername",
                email
        );

        when(repository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Notification actualNotification = service.saveUserRegisteredEvent(event);

        assertNotNull(actualNotification);
        assertEquals(actualNotification.getUserId(), userId);
        assertEquals(actualNotification.getStatus(), Status.PENDING);
        assertEquals(actualNotification.getEventPayload(), event);

        verify(repository).save(any(Notification.class));
    }

    @Test
    void saveUserRegisteredEvent_repositoryThrowsException() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "testUsername",
                "test@gmail.com"
        );

        when(repository.save(any(Notification.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> service.saveUserRegisteredEvent(event));

        verify(repository).save(any(Notification.class));
    }

    @Test
    void sendWelcomeNotification_success() {
        UUID userId = UUID.randomUUID();
        String email = "test@gmail.com";
        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                userId,
                "testUsername",
                email
        );
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .email(email)
                .status(Status.PENDING)
                .eventPayload(event)
                .build();

        doNothing().when(sender).send(notification.getEmail(),
                "Welcome!", "Hello %s".formatted(notification.getEventPayload().username()));
        when(repository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.sendWelcomeNotification(notification);

        assertNotNull(notification);
        assertEquals(Status.SAVE, notification.getStatus());

        verify(sender).send(notification.getEmail(),
                "Welcome!", "Hello %s".formatted(notification.getEventPayload().username()));
        verify(repository).save(any(Notification.class));
    }

    @Test
    void sendWelcomeNotification_senderThrowsException() {
        UUID userId = UUID.randomUUID();
        String email = "test@gmail.com";
        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                userId,
                "testUsername",
                email
        );
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
                .email(email)
                .status(Status.PENDING)
                .eventPayload(event)
                .build();

        doThrow(RuntimeException.class).when(sender).send(notification.getEmail(),
                "Welcome!",  "Hello %s".formatted(notification.getEventPayload().username()));
        when(repository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.sendWelcomeNotification(notification);

        assertEquals(notification.getStatus(), Status.FAIL);

        verify(sender).send(notification.getEmail(),
                "Welcome!",  "Hello %s".formatted(notification.getEventPayload().username()));
        verify(repository).save(any(Notification.class));
    }

    @Test
    void sendWelcomeNotification_repositoryThrowsException() {
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
                .email("test@gmail.com")
                .status(Status.PENDING)
                .build();

        when(repository.save(any(Notification.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> service.sendWelcomeNotification(notification));

        verify(repository).save(any(Notification.class));
    }

    @Test
    void getAllByStatus_success() {
        String expectedEmail1 = "test1@gmail.com";
        String expectedEmail2 = "test2@gmail.com";
        Notification notification1 = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
                .email(expectedEmail1)
                .status(Status.SAVE)
                .build();
        Notification notification2 = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
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
                notification1.getId(),
                notification1.getEmail(),
                notification1.getStatus(),
                notification1.getCreatedAt()));
        when(mapper.toResponse(notification2)).thenReturn(new NotificationResponse(
                notification2.getId(),
                notification2.getEmail(),
                notification2.getStatus(),
                notification2.getCreatedAt()));

        Page<NotificationResponse> notificationResponsePage = service.getAllByStatus(Status.SAVE, pageable);

        assertNotNull(notificationResponsePage);
        assertEquals(2, notificationResponsePage.getContent().size());
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
        String expectedEmail = "test1@gmail.com";
        Notification notification1 = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .email(expectedEmail)
                .status(Status.SAVE)
                .build();
        Notification notification2 = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
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
                notification1.getId(),
                notification1.getEmail(),
                notification1.getStatus(),
                notification1.getCreatedAt()));
        when(mapper.toResponse(notification2)).thenReturn(new NotificationResponse(
                notification2.getId(),
                notification2.getEmail(),
                notification2.getStatus(),
                notification2.getCreatedAt()));

        Page<NotificationResponse> notificationResponsePage = service.getAllByUserId(userId, pageable);

        assertNotNull(notificationResponsePage);
        assertEquals(2, notificationResponsePage.getContent().size());
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
