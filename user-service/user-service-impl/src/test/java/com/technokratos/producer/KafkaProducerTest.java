package com.technokratos.producer;

import com.technokratos.model.OutboxEventEntity;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {
    @InjectMocks
    private KafkaProducer producer;
    @Mock
    private KafkaTemplate<String, String> template;

    /*
     * Для unit-тестов используем синхронный executor.
     *
     * Это позволяет выполнять callback-и в том же потоке и
     * не использовать timeout(), Awaitility и прочие механизмы
     * ожидания асинхронного выполнения.
     */
    private Executor outboxExecutor;

    @BeforeEach
    void setUp() {
        outboxExecutor = Runnable::run;

        producer = new KafkaProducer(
                template,
                outboxExecutor
        );
    }

    @Test
    void publishEvent_success() {
        UUID eventId = UUID.randomUUID();

        OutboxEventEntity entity = OutboxEventEntity.builder()
                .id(eventId)
                .topic("user-topic")
                .aggregateId("user-1")
                .type("user-registered")
                .payload("{\"id\":\"1\"}")
                .build();

        SendResult<String, Object> sendResult = mock(SendResult.class);
        RecordMetadata metadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(metadata);
        when(metadata.offset()).thenReturn(15L);

        when(template.send(any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        Runnable onSuccess = mock(Runnable.class);
        Consumer<Throwable> onFailure = mock(Consumer.class);

        producer.publishEvent(entity, onSuccess, onFailure);

        /*
         * При успешной отправке callback успеха должен выполниться,
         * а callback ошибки не должен вызываться.
         */
        verify(onSuccess).run();
        verify(onFailure, never()).accept(any());
    }

    @Test
    void publishEvent_failure() {
        UUID eventId = UUID.randomUUID();

        OutboxEventEntity entity = OutboxEventEntity.builder()
                .id(eventId)
                .topic("user-topic")
                .aggregateId("user-1")
                .type("user-registered")
                .payload("{\"id\":\"1\"}")
                .build();

        RuntimeException kafkaException =
                new RuntimeException("Kafka unavailable");

        when(template.send(any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.failedFuture(kafkaException));

        Runnable onSuccess = mock(Runnable.class);
        Consumer<Throwable> onFailure = mock(Consumer.class);

        producer.publishEvent(entity, onSuccess, onFailure);

        /*
         * Любая ошибка Kafka должна переводиться
         * в вызов callback-а обработки ошибки.
         */
        verify(onFailure).accept(any(Throwable.class));
        verify(onSuccess, never()).run();
    }

    @Test
    void publishEvent_addsTypeIdHeader() {
        UUID eventId = UUID.randomUUID();

        OutboxEventEntity entity = OutboxEventEntity.builder()
                .id(eventId)
                .topic("user-topic")
                .aggregateId("user-1")
                .type("user-registered")
                .payload("{\"id\":\"1\"}")
                .build();

        SendResult<String, String> sendResult = mock(SendResult.class);
        RecordMetadata metadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(metadata);
        when(metadata.offset()).thenReturn(1L);

        when(template.send(any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        ArgumentCaptor<ProducerRecord<String, String>> captor =
                ArgumentCaptor.forClass(ProducerRecord.class);

        producer.publishEvent(
                entity,
                () -> {},
                ex -> {}
        );

        verify(template).send(captor.capture());

        ProducerRecord<String, String> record = captor.getValue();

        Header header = record.headers().lastHeader("__TypeId__");

        /*
         * Проверяем передачу типа события через Kafka Header.
         *
         * Consumer использует этот заголовок для выбора
         * класса события через TypeMappings.
         */
        assertNotNull(header);

        assertEquals(
                entity.getType(),
                new String(header.value(), StandardCharsets.UTF_8)
        );
    }

    @Test
    void publishEvent_buildsProducerRecord() {
        UUID eventId = UUID.randomUUID();

        OutboxEventEntity entity = OutboxEventEntity.builder()
                .id(eventId)
                .topic("user-topic")
                .aggregateId("aggregate-123")
                .type("user-registered")
                .payload("{\"id\":\"1\"}")
                .build();

        SendResult<String, String> sendResult = mock(SendResult.class);
        RecordMetadata metadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(metadata);
        when(metadata.offset()).thenReturn(1L);

        when(template.send(any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        ArgumentCaptor<ProducerRecord<String, String>> captor =
                ArgumentCaptor.forClass(ProducerRecord.class);

        producer.publishEvent(
                entity,
                () -> {},
                ex -> {}
        );

        verify(template).send(captor.capture());

        ProducerRecord<String, String> record = captor.getValue();

        /*
         * Проверяем корректное формирование Kafka Record
         * из данных Outbox-события.
         */
        assertEquals(entity.getTopic(), record.topic());
        assertEquals(entity.getAggregateId(), record.key());
        assertEquals(entity.getPayload(), record.value());
    }
}