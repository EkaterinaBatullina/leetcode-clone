package com.technokratos.service;

import com.technokratos.dto.response.NotificationResponse;
import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.mapper.NotificationMapper;
import com.technokratos.model.Notification;
import com.technokratos.dto.enums.Status;
import com.technokratos.repository.NotificationRepository;
import com.technokratos.sender.EmailSender;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final EmailSender sender;
    private final NotificationMapper mapper;
    private final NotificationRepository repository;
    private final NotificationMetricsService service;

    @Override
    public Notification saveUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Saving notification event for userId={} email={}", event.userId(), event.email());
        Notification notification = Notification.builder()
                .userId(event.userId())
                .email(event.email())
                .status(Status.PENDING)
                .createdAt(Instant.now())
                .username(event.username())
                .build();
        return repository.save(notification);
    }

    @Override
    public void sendWelcomeNotification(Notification notification) {
        log.info("Sending welcome notification to userId={} email={}",
                notification.getUserId(), notification.getEmail());
        Timer.Sample sample = service.startTimer();
        try {
            sender.send(notification.getEmail(),
                    "Welcome!",
                    "Hello %s".formatted(notification.getUsername()));
            notification.setStatus(Status.SAVE);
            log.info("Notification sent successfully to userId={}", notification.getUserId());
            service.incrementSent();
        } catch (Exception e) {
            notification.setStatus(Status.FAIL);
            notification.setErrorType(e.getClass().getSimpleName());
            notification.setErrorMessage(e.getMessage());
            log.error("Failed to send notification to userId={}: {}",
                    notification.getUserId(), e.getMessage(), e);
            service.incrementFailed();
        }
        service.stopTimer(sample);
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
