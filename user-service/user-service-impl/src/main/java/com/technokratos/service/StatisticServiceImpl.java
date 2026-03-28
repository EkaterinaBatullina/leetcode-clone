package com.technokratos.service;

import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.exception.StatisticsNotFoundException;
import com.technokratos.mapper.StatisticMapper;
import com.technokratos.model.StatisticEntity;
import com.technokratos.repository.StatisticRepository;
import com.technokratos.dto.request.UserUpdateRequest;
import com.technokratos.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.technokratos.enums.SubmissionStatus.SOLVED;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository repository;
    private final StatisticMapper mapper;

    @Override
    @Cacheable(value = "statistic", key = "T(com.technokratos.util.SecurityUtil).getCurrentUserId()")
    public StatisticResponse getById() {
        UUID uuid = SecurityUtil.getCurrentUserId();
        return mapper.toResponse(
                repository.findById(uuid)
                        .orElseThrow(() -> new StatisticsNotFoundException(uuid))
        );
    }

    @Override
    public void create(UUID uuid) {
        StatisticEntity statisticEntity =
                new StatisticEntity(uuid, 0, 0, 0, 0, 0, 0);
        repository.save(statisticEntity);
    }

    @Override
    @CacheEvict(value = "statistic", key = "#request.userId")
    public void update(UserUpdateRequest request) {
        StatisticEntity statistic = repository.findById(request.userId())
                .orElseThrow(() -> new StatisticsNotFoundException(request.userId()));

        int solvedDelta = 0;
        int easyDelta = 0;
        int mediumDelta = 0;
        int hardDelta = 0;

        if (SOLVED.equals(request.status()) && request.isFirstSuccessfulAttempt()) {
            solvedDelta = 1;
            switch (request.difficulty()) {
                case EASY -> easyDelta = 1;
                case MEDIUM -> mediumDelta = 1;
                case HARD -> hardDelta = 1;
            }
        }

        repository.update(request.userId(), solvedDelta, easyDelta, mediumDelta, hardDelta);
    }
}