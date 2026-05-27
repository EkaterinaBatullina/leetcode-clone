package com.technokratos.repository;

import com.technokratos.dto.enums.Status;
import com.technokratos.model.OutboxEventEntity;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository {

    void save(OutboxEventEntity entity);

    List<OutboxEventEntity> findAllNew(int limit);

    void updateStatus(UUID id, Status status);
}
