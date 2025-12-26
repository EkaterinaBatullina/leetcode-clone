package com.technokratos.consumer;

import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.model.Notification;
import com.technokratos.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final NotificationService service;

    @RetryableTopic(
            kafkaTemplate = "kafkaTemplate",
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 5000)
    )
    @KafkaListener(topics = "${spring.kafka.topic.user-registered}",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserRegisteredEvent(UserRegisteredEvent event, Acknowledgment ack) {
        Notification notification = service.saveUserRegisteredEvent(event);
        service.sendWelcomeNotification(notification);
        log.info("Successfully processed UserRegisteredEvent for userId: {}, email: {}",
                event.userId(),
                event.email());
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.user-registered}-dlt",
            groupId = "notification-dlt-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeDLQ(UserRegisteredEvent event) {
        log.error("Failed to process UserRegisteredEvent after retries: {}", event);
    }
}
