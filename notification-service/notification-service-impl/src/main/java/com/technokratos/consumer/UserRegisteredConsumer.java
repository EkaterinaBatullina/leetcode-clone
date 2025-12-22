package com.technokratos.consumer;

import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.model.Notification;
import com.technokratos.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegisteredConsumer {
    private final NotificationService service;

    @KafkaListener(topics = "user-registered-event",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserRegisteredEvent(UserRegisteredEvent event) {
        Notification notification = service.saveUserRegisteredEvent(event);
        service.sendWelcomeNotification(notification);
    }
}
