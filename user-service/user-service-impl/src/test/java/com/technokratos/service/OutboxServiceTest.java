package com.technokratos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.config.property.KafkaProducerProperties;
import com.technokratos.dto.enums.Status;
import com.technokratos.event.UserRegisteredEvent;
import com.technokratos.model.OutboxEntity;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
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
        OutboxEntity entity = OutboxEntity.builder()
                .id(eventId)
                .status(Status.NEW)
                .build();

        when(repository.findAllNew(50)).thenReturn(List.of(entity));

        doAnswer(invocation -> {
            Runnable onSuccess = invocation.getArgument(1);
            onSuccess.run();
            return null;
        }).when(producer).publishEvent(any(), any(), any());

        service.processOutbox();

        verify(repository).findAllNew(50);
        verify(repository).updateStatus(eventId, Status.SENT);
    }

    @Test
    void processOutbox_failed() {
        UUID eventId = UUID.randomUUID();
        OutboxEntity entity = OutboxEntity.builder()
                .id(eventId)
                .status(Status.NEW)
                .build();

        when(repository.findAllNew(50)).thenReturn(List.of(entity));

        doAnswer(invocation -> {
            Consumer<Exception> onError = invocation.getArgument(2);
            onError.accept(new RuntimeException("Kafka down"));
            return null;
        }).when(producer).publishEvent(any(), any(), any());

        service.processOutbox();

        verify(repository).findAllNew(50);
        verify(repository).updateStatus(eventId, Status.FAILED);
    }

    @Test
    void saveUserRegisteredOutboxEvent() throws JsonProcessingException {
        UUID userId = UUID.randomUUID();
        String username = "testUser";
        String email = "email@gmail.ru";
        String topic = "user-registered-topic";
        String payload = "{\"userId\":\"" + userId + "\"}";

        when(properties.getUserRegisteredTopic()).thenReturn(topic);
        when(mapper.writeValueAsString(any(UserRegisteredEvent.class))).thenReturn(payload);
        doNothing().when(repository).save(any(OutboxEntity.class));

        service.saveUserRegisteredOutboxEvent(userId, username, email);

        verify(properties).getUserRegisteredTopic();
        verify(mapper).writeValueAsString(any(UserRegisteredEvent.class));
        verify(repository).save(argThat(entity ->
                entity.getAggregateId().equals(userId.toString()) &&
                        entity.getTopic().equals(topic) &&
                        entity.getStatus() == Status.NEW &&
                        entity.getPayload().equals(payload)
        ));
    }

    @Test
    void saveUserRegisteredOutboxEvent_serializationError() throws JsonProcessingException {
        UUID userId = UUID.randomUUID();
        when(mapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () ->
                service.saveUserRegisteredOutboxEvent(userId, "user", "email@gmail.ru"));

        verify(repository, never()).save(any());
    }
}
