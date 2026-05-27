package com.technokratos.scheduler;

import com.technokratos.dto.enums.Status;
import com.technokratos.model.Notification;
import com.technokratos.repository.NotificationRepository;
import com.technokratos.service.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationRetrySchedulerTest {
    @Mock
    private NotificationRepository repository;
    @Mock
    private NotificationServiceImpl notificationService;
    @InjectMocks
    private NotificationRetryScheduler scheduler;

    @Test
    void processFailedNotifications_shouldRetryWhenFailedExists() {
        Notification failedNotification = Notification.builder()
                .id("test-id")
                .status(Status.FAIL)
                .email("test@gmail.com")
                .build();

        when(repository.findByStatus(Status.FAIL)).thenReturn(List.of(failedNotification));
        doNothing().when(notificationService).retryWelcomeNotification(any(Notification.class));

        scheduler.processFailedNotifications();

        verify(repository).findByStatus(Status.FAIL);
        verify(notificationService).retryWelcomeNotification(failedNotification);
    }

    @Test
    void processFailedNotifications_shouldDoNothingWhenNoFailed() {
        when(repository.findByStatus(Status.FAIL)).thenReturn(List.of());

        scheduler.processFailedNotifications();

        verify(repository).findByStatus(Status.FAIL);
        verifyNoInteractions(notificationService);
    }
}
