package com.technokratos.producer;

import com.technokratos.model.OutboxEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.function.Consumer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> template;
    private final Executor outboxExecutor;

    public void publishEvent(OutboxEntity entity, Runnable onSuccess, Consumer<Throwable> onFailure) {
        template.send(entity.getTopic(), entity.getAggregateId(), entity.getPayload())
                .thenAcceptAsync(result -> {
                    log.info("Event sent! Topic: {}, Offset: {}", entity.getTopic(), result.getRecordMetadata().offset());
                    onSuccess.run();
                }, outboxExecutor)
                .exceptionally(ex -> {
                    log.error("Failed to send event {}. Reason: {}", entity.getId(), ex.getMessage());
                    onFailure.accept(ex.getCause() != null ? ex.getCause() : ex);
                    return null;
                });
    }
}
