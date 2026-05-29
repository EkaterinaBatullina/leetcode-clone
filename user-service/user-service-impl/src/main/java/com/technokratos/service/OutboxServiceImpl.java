package com.technokratos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.dto.enums.Status;
import com.technokratos.model.OutboxEventEntity;
import com.technokratos.producer.KafkaProducer;
import com.technokratos.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ObjectMapper mapper;

    @Override
    @Transactional
    public void processOutbox() {
        List<OutboxEventEntity> pending = repository.pollAndLock(50);

        for (OutboxEventEntity entity : pending) {
            producer.publishEvent(
                    entity,
                    () -> updateStatus(entity.getId(), Status.SENT),
                    ex -> {
                        log.error("Failed to send outbox event {}", entity.getId(), ex);
                        updateStatus(entity.getId(), Status.NEW);
                    }
            );
        }
    }

    @Override
    public void recoverStuckEvents() {
        repository.resetStuckProcessingEvents();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(UUID id, Status status) {
        repository.updateStatus(id, status);
    }

    @Override
    public void save(String topic, String aggregateId, String type, Object event) {
        try {
            repository.save(OutboxEventEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateId(aggregateId)
                    .type(type)
                    .payload(mapper.writeValueAsString(event))
                    .topic(topic)
                    .status(Status.NEW)
                    .build());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize outbox event. Type: {}, AggregateId: {}, Topic: {}",
                    type, aggregateId, topic, e);
            throw new RuntimeException("Error during outbox event serialization for type: " + type, e);
        }
    }
}
