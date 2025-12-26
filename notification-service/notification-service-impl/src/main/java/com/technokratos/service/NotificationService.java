package com.technokratos.service;

import com.technokratos.dto.response.NotificationResponse;
import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.model.Notification;
import com.technokratos.dto.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

    Notification saveUserRegisteredEvent(UserRegisteredEvent event);

    void sendWelcomeNotification(Notification notification);

    Page<NotificationResponse> getAllByStatus(Status status, Pageable pageable);

    Page<NotificationResponse> getAllByUserId(UUID userId, Pageable pageable);
}
