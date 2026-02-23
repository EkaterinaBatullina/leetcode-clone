package com.technokratos.repository;

import com.technokratos.dto.enums.Status;
import com.technokratos.model.OutboxEntity;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository {

    void save(OutboxEntity entity);

    List<OutboxEntity> findAllNew(int limit);

    void updateStatus(UUID id, Status status);
}
