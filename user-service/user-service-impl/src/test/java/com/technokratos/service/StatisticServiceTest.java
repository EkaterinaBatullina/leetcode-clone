package com.technokratos.service;

import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.exception.StatisticsNotFoundException;
import com.technokratos.mapper.StatisticMapper;
import com.technokratos.model.StatisticEntity;
import com.technokratos.enums.Difficulty;
import com.technokratos.repository.StatisticRepository;
import com.technokratos.dto.request.UserUpdateRequest;
import com.technokratos.enums.SubmissionStatus;
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
class StatisticServiceTest {
    @InjectMocks StatisticServiceImpl service;
    @Mock StatisticRepository repository;
    @Mock StatisticMapper mapper;

    @Test
    void getById() {
        UUID uuid = UUID.randomUUID();
        StatisticEntity entity = new StatisticEntity(uuid, 0, 0, 0, 0, 0, 0);
        StatisticResponse expected = new StatisticResponse(uuid, 0, 0, 0, 0, 0, 0);

        try (MockedStatic<SecurityUtil> utils = mockStatic(SecurityUtil.class)) {
            utils.when(SecurityUtil::getCurrentUserId).thenReturn(uuid);
            when(repository.findById(uuid)).thenReturn(Optional.of(entity));
            when(mapper.toResponse(entity)).thenReturn(expected);

            assertEquals(expected, service.getById());
        }
    }

    @Test
    void getById_notFound() {
        UUID uuid = UUID.randomUUID();
        try (MockedStatic<SecurityUtil> utils = mockStatic(SecurityUtil.class)) {
            utils.when(SecurityUtil::getCurrentUserId).thenReturn(uuid);
            when(repository.findById(uuid)).thenReturn(Optional.empty());

            assertThrows(StatisticsNotFoundException.class, () -> service.getById());
        }
    }

    @Test
    void create() {
        UUID uuid = UUID.randomUUID();

        service.create(uuid);

        verify(repository).save(argThat(entity -> entity.getUserId().equals(uuid)));
    }

    @Test
    void update() {
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(userId, Difficulty.EASY, SubmissionStatus.SOLVED, true);

        service.update(request);

        verify(repository).update(userId, 1, 1, 0, 0);
    }
}
