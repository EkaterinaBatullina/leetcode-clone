package com.technokratos.producer;

import com.technokratos.model.OutboxEventEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> template;
    private final Executor outboxExecutor;

    public void publishEvent(OutboxEventEntity entity, Runnable onSuccess, Consumer<Throwable> onFailure) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                entity.getTopic(),
                entity.getAggregateId(),
                entity.getPayload()
        );

        record.headers().add(
                "__TypeId__",
                entity.getType().getBytes(StandardCharsets.UTF_8)
        );

        template.send(record)
                .thenAcceptAsync(result -> {
                    log.info("Event sent! Topic: {}, Offset: {}", entity.getTopic(), result.getRecordMetadata().offset());
                    onSuccess.run();
                }, outboxExecutor)
                .exceptionallyAsync(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    if (cause instanceof RejectedExecutionException) {
                        log.warn("Outbox thread pool is overflowed! Event {} will remain pending for retry.", entity.getId());
                    } else {
                        log.error("Permanent failure for event {}. Reason: {}", entity.getId(), cause.getMessage());
                        onFailure.accept(cause);
                    }
                    return null;
                }, outboxExecutor);
    }
}
