package com.technokratos.problemserviceimpl.service;

import com.technokratos.problemserviceapi.dto.request.PublishTestcasesRequest;
import com.technokratos.problemserviceapi.enums.PublishStatus;
import com.technokratos.problemserviceimpl.kafka.producer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishingCoordinatorService {
    private final KafkaProducerService kafkaProducerService;
    private final ConcurrentMap<UUID, CompletableFuture<PublishStatus>> publishAckMap = new ConcurrentHashMap<>();

    public CompletableFuture<PublishStatus> publishWithAck(PublishTestcasesRequest request) {
        CompletableFuture<PublishStatus> ackFuture = new CompletableFuture<>();
        publishAckMap.put(request.problemId(), ackFuture);
        kafkaProducerService.sendEventToPublishTestcasesTopic(request);
        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(() -> {
            ackFuture.complete(PublishStatus.FAILED);
        });
        return ackFuture;
    }

    public void completeAck(UUID problemId, PublishStatus status) {
        CompletableFuture<PublishStatus> future = publishAckMap.remove(problemId);
        if (future != null) {
            future.complete(status);
        }
    }
}

