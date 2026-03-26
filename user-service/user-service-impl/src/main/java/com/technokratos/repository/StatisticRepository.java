package com.technokratos.repository;

import com.technokratos.model.StatisticEntity;

import java.util.Optional;
import java.util.UUID;

public interface StatisticRepository {

    Optional<StatisticEntity> findById(UUID uuid);

    void save(StatisticEntity statisticEntity);

    void update(UUID userId, int solvedDelta, int easyDelta, int mediumDelta, int hardDelta);
}