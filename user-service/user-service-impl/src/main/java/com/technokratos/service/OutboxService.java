package com.technokratos.service;

import com.technokratos.dto.enums.Status;

import java.util.UUID;

public interface OutboxService {

    void processOutbox();

    void recoverStuckEvents();

    void updateStatus(UUID id, Status status);

    void save(String topic, String aggregateId, String type, Object eventPayload);
}
