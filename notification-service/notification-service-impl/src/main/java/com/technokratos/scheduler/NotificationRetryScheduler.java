package com.technokratos.scheduler;

import com.technokratos.dto.enums.Status;
import com.technokratos.model.Notification;
import com.technokratos.repository.NotificationRepository;
import com.technokratos.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRetryScheduler {
    private final NotificationRepository repository;
    private final NotificationServiceImpl notificationService;

    @Scheduled(fixedDelay = 60000)
    public void processFailedNotifications() {
        log.debug("Scheduler started: scanning MongoDB for failed notifications...");

        List<Notification> failedNotifications = repository.findByStatus(Status.FAIL);

        if (failedNotifications.isEmpty()) {
            return;
        }

        log.info("Found {} failed notifications to retry", failedNotifications.size());

        for (Notification notification : failedNotifications) {
            try {
                notificationService.retryWelcomeNotification(notification);
            } catch (Exception e) {
                log.error("Unexpected error during scheduler retry for eventId={}", notification.getId(), e);
            }
        }
    }
}

