package com.technokratos.service;

import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.exception.StatisticsNotFoundException;
import com.technokratos.mapper.StatisticMapper;
import com.technokratos.model.StatisticEntity;
import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.repository.StatisticRepository;
import com.technokratos.submissionserviceapi.dto.request.UserUpdateRequest;
import com.technokratos.submissionserviceapi.enums.SubmissionStatus;
import com.technokratos.util.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatisticServiceTest {
    @InjectMocks
    StatisticServiceImpl service;
    @Mock
    StatisticRepository repository;
    @Mock
    StatisticMapper mapper;

    @Test
    void getById() {
        UUID expectedUuid = UUID.randomUUID();
        StatisticEntity statisticEntity =
                new StatisticEntity(expectedUuid, 0, 0, 0, 0, 0, 0);
        StatisticResponse expectedResponse =
                new StatisticResponse(expectedUuid, 0, 0, 0, 0, 0, 0);

        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserId).thenReturn(expectedUuid);

            when(repository.findById(expectedUuid)).thenReturn(Optional.of(statisticEntity));
            when(mapper.toResponse(statisticEntity)).thenReturn(expectedResponse);

            StatisticResponse response = service.getById();

            assertEquals(expectedResponse, response);
            verify(repository).findById(expectedUuid);
            verify(mapper).toResponse(statisticEntity);
        }
    }

    @Test
    void getById_notFound() {
        UUID uuid = UUID.randomUUID();
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserId).thenReturn(uuid);

            when(repository.findById(uuid)).thenReturn(Optional.empty());

            assertThrows(StatisticsNotFoundException.class, () -> service.getById());
            verify(repository).findById(uuid);
        }
    }

    @Test
    void create() {
        UUID expectedUuid = UUID.randomUUID();
        doNothing().when(repository).save(any(StatisticEntity.class));
        service.create(expectedUuid);
        verify(repository).save(argThat(statisticEntity -> statisticEntity.getUserId().equals(expectedUuid)));
    }

    @Test
    void update() {
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(userId, Difficulty.EASY, SubmissionStatus.SOLVED, true);

        StatisticEntity statisticEntity =
                new StatisticEntity(userId, 1, 1, 0, 0, 0, 0);
        when(repository.findById(userId)).thenReturn(Optional.of(statisticEntity));

        doNothing().when(repository).update(userId, 1, 1, 0, 0);

        service.update(request);

        verify(repository).findById(userId);
        verify(repository).update(userId, 1, 1, 0, 0);
    }

    @Test
    void update_notFound() {
        UUID uuid = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(uuid, Difficulty.EASY, SubmissionStatus.SOLVED, true);

        when(repository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(StatisticsNotFoundException.class, () -> service.update(request));

        verify(repository).findById(uuid);
        verify(repository, never()).update(any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt());
    }
}