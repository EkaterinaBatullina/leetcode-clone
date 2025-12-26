package com.technokratos.producer;

import com.technokratos.config.property.KafkaProducerProperties;
import com.technokratos.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, UserRegisteredEvent> template;
    private final KafkaProducerProperties properties;

    public void publishEvent(UUID userId, String username, String email) {
        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                userId,
                username,
                email
        );
        template.send(properties.getUserRegisteredTopic(), userId.toString(), event);
    }
}
