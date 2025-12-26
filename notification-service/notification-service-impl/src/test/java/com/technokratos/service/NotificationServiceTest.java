package com.technokratos.service;

import com.technokratos.dto.enums.Status;
import com.technokratos.dto.response.NotificationResponse;
import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.mapper.NotificationMapper;
import com.technokratos.model.Notification;
import com.technokratos.repository.NotificationRepository;
import com.technokratos.sender.EmailSender;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    void saveUserRegisteredEvent_success() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "username",
                "test@gmail.com"
        );
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID())
                .email("test@gmail.com")
                .status(Status.PENDING)
                .createdAt(Instant.now())
                .eventPayload(any(UserRegisteredEvent.class))
                .build();

        when(repository.save(notification)).thenReturn(notification);

        Notification notificationResult = service.saveUserRegisteredEvent(event);

        assertEquals(notificationResult.getStatus(), Status.SAVE);

        verify(repository.save(notification));
    }

    @Override
    public Notification saveUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Saving notification event for userId={} email={}", event.userId(), event.email());
        Notification notification = Notification.builder()
                .userId(event.userId())
                .email(event.email())
                .status(Status.PENDING)
                .createdAt(Instant.now())
                .eventPayload(event)
                .build();
        return repository.save(notification);
    }

    @Override
    public void sendWelcomeNotification(Notification notification) {
        log.info("Sending welcome notification to userId={} email={}",
                notification.getUserId(), notification.getEmail());
        try {
            sender.send(notification.getEmail(),
                    "Welcome!",
                    "Hello %s".formatted(notification.getEventPayload().username()));
            notification.setStatus(Status.SAVE);
            log.info("Notification sent successfully to userId={}", notification.getUserId());
        } catch (Exception e) {
            notification.setStatus(Status.FAIL);
            notification.setErrorType(e.getClass().getSimpleName());
            notification.setErrorMessage(e.getMessage());
            log.error("Failed to send notification to userId={}: {}",
                    notification.getUserId(), e.getMessage(), e);
        }
        repository.save(notification);
    }

    @Override
    public Page<NotificationResponse> getAllByStatus(Status status, Pageable pageable) {
        log.info("Fetching notifications by status={} page={}", status, pageable.getPageNumber());
        return repository.findByStatus(status, pageable).map(mapper::toResponse);
    }

    @Override
    public Page<NotificationResponse> getAllByUserId(UUID userId, Pageable pageable) {
        log.info("Fetching notifications for userId={} page={}", userId, pageable.getPageNumber());
        return repository.findByUserId(userId, pageable).map(mapper::toResponse);
    }
}
