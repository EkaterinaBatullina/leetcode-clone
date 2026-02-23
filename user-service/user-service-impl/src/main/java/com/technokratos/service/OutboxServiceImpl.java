package com.technokratos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.config.property.KafkaProducerProperties;
import com.technokratos.dto.enums.Status;
import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.model.OutboxEntity;
import com.technokratos.producer.KafkaProducer;
import com.technokratos.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {
    private final OutboxRepository repository;
    private final KafkaProducer producer;
    private final KafkaProducerProperties properties;
    private final ObjectMapper mapper;

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void processOutbox() {
        List<OutboxEntity> pending = repository.findAllNew(50);

        for (OutboxEntity entity : pending) {
            producer.publishEvent(
                    entity,
                    () -> updateStatus(entity.getId(), Status.SENT),
                    ex -> updateStatus(entity.getId(), Status.FAILED)
            );
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(UUID id, Status status) {
        repository.updateStatus(id, status);
    }

    public void saveUserRegisteredOutboxEvent(UUID userId, String username, String email) {
        UserRegisteredEvent event = new UserRegisteredEvent(UUID.randomUUID(), userId, username, email);
        try {
            repository.save(OutboxEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateId(userId.toString())
                    .type("USER_REGISTERED")
                    .payload(mapper.writeValueAsString(event))
                    .topic(properties.getUserRegisteredTopic())
                    .status(Status.NEW)
                    .build());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UserRegisteredEvent for userId: {}", userId, e);
            throw new RuntimeException("Error during user registration event serialization", e);
        }
    }
}
