package com.technokratos.service;

import com.technokratos.dto.enums.Status;
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
}
