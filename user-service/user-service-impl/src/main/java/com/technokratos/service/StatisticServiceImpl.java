package com.technokratos.service;

import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.exception.StatisticsNotFoundException;
import com.technokratos.mapper.StatisticMapper;
import com.technokratos.model.StatisticEntity;
import com.technokratos.repository.StatisticRepository;
import com.technokratos.submissionserviceapi.dto.request.UserUpdateRequest;
import com.technokratos.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.technokratos.submissionserviceapi.enums.SubmissionStatus.SOLVED;

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
        StatisticEntity statisticEntity = repository.findById(request.userId())
                .orElseThrow(() -> new StatisticsNotFoundException(request.userId()));
        statisticEntity.setAttempts(statisticEntity.getAttempts() + 1);
        if (SOLVED.equals(request.status())) {
            if (request.isFirstSuccessfulAttempt()) {
                statisticEntity.setSolvedTasks(statisticEntity.getSolvedTasks() + 1);
                switch (request.difficulty()) {
                    case EASY -> statisticEntity.setEasy(statisticEntity.getEasy() + 1);
                    case MEDIUM -> statisticEntity.setMedium(statisticEntity.getMedium() + 1);
                    case HARD -> statisticEntity.setHard(statisticEntity.getHard() + 1);
                }
            }
        }
        statisticEntity.setSuccessPercentage((int) ((double) statisticEntity.getSolvedTasks()
                / statisticEntity.getAttempts() * 100));
        repository.update(statisticEntity);
    }
}