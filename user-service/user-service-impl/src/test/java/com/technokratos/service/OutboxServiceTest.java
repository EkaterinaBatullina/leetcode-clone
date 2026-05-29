package com.technokratos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.config.property.KafkaProducerProperties;
import com.technokratos.dto.enums.Status;
import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.model.OutboxEventEntity;
import com.technokratos.producer.KafkaProducer;
import com.technokratos.repository.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutboxServiceTest {
    @InjectMocks
    OutboxServiceImpl service;
    @Mock
    OutboxRepository repository;
    @Mock
    KafkaProducer producer;
    @Mock
    KafkaProducerProperties properties;
    @Mock
    ObjectMapper mapper;

    @Test
    void processOutbox() {
        UUID eventId = UUID.randomUUID();

        OutboxEventEntity entity = OutboxEventEntity.builder()
                .id(eventId)
                .status(Status.PROCESSING)
                .build();

        when(repository.pollAndLock(50)).thenReturn(List.of(entity));

        doAnswer(invocation -> {
            Runnable onSuccess = invocation.getArgument(1);
            onSuccess.run();
            return null;
        }).when(producer).publishEvent(any(), any(), any());

        service.processOutbox();

        verify(repository).pollAndLock(50);
        verify(repository).updateStatus(eventId, Status.SENT);
    }

    @Test
    void processOutbox_failed() {
        UUID eventId = UUID.randomUUID();

        OutboxEventEntity entity = OutboxEventEntity.builder()
                .id(eventId)
                .status(Status.PROCESSING)
                .build();

        when(repository.pollAndLock(50)).thenReturn(List.of(entity));

        doAnswer(invocation -> {
            Consumer<Throwable> onError = invocation.getArgument(2);
            onError.accept(new RuntimeException("Kafka down"));
            return null;
        }).when(producer).publishEvent(any(), any(), any());

        service.processOutbox();

        verify(repository).pollAndLock(50);
        verify(repository).updateStatus(eventId, Status.NEW);
    }

    @Test
    void save_success() throws JsonProcessingException {
        String topic = "user-registered-topic";
        String aggregateId = UUID.randomUUID().toString();
        String type = "user-registered";

        UserRegisteredEvent testEvent =
                new UserRegisteredEvent(UUID.randomUUID(), UUID.fromString(aggregateId), "testUser", "email@gmail.ru");

        when(mapper.writeValueAsString(testEvent)).thenReturn("json");

        service.save(topic, aggregateId, type, testEvent);

        verify(mapper).writeValueAsString(testEvent);

        verify(repository).save(argThat(entity ->
                entity.getAggregateId().equals(aggregateId) &&
                        entity.getTopic().equals(topic) &&
                        entity.getType().equals(type) &&
                        entity.getStatus() == Status.NEW &&
                        entity.getPayload().equals("json")
        ));
    }

    @Test
    void save_serializationError() throws JsonProcessingException {
        String topic = "user-registered-topic";
        String aggregateId = UUID.randomUUID().toString();
        String type = "user-registered";
        Object dummyEvent = new Object();

        JsonProcessingException jacksonException =
                new JsonProcessingException("boom") {};

        when(mapper.writeValueAsString(any()))
                .thenThrow(jacksonException);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.save(topic, aggregateId, type, dummyEvent));

        assertTrue(exception.getMessage()
                .contains("Error during outbox event serialization for type: " + type));

        assertEquals(jacksonException, exception.getCause());

        verify(repository, never()).save(any());
    }
}
