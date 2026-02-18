package com.technokratos.consumer;

import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.exception.DuplicateEventException;
import com.technokratos.model.Notification;
import com.technokratos.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final NotificationService service;

    @RetryableTopic(
            kafkaTemplate = "kafkaTemplate",
            backoff = @Backoff(delay = 10, multiplier = 1, maxDelay = 50)
    )
    @KafkaListener(topics = "${spring.kafka.topic.user-registered}",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserRegisteredEvent(UserRegisteredEvent event, Acknowledgment ack) {
        try {
            Notification notification = service.saveUserRegisteredEvent(event);
            service.sendWelcomeNotification(notification);
            log.info("Successfully processed UserRegisteredEvent for userId: {}, email: {}",
                    event.userId(),
                    event.email());
            ack.acknowledge();
        } catch (DuplicateEventException e) {
            log.info("Skipping duplicate notification for event: {}", event.eventId());
            ack.acknowledge();
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.user-registered}-dlt",
            groupId = "notification-dlt-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeDLQ(UserRegisteredEvent event,
                           @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String errorMessage,
                           @Header(KafkaHeaders.DLT_EXCEPTION_FQCN) String errorClass,
                           @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic) {
        log.error(
                "DLT event failed. topic={}, userId={}, error={}, message={}",
                originalTopic,
                event.userId(),
                errorClass,
                errorMessage
        );
    }
}
