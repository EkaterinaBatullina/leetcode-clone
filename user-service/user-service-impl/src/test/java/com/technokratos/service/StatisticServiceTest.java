package com.technokratos.service;

import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.mapper.StatisticMapper;
import com.technokratos.model.StatisticEntity;
import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.repository.StatisticRepository;
import com.technokratos.submissionserviceapi.dto.request.UserUpdateRequest;
import com.technokratos.submissionserviceapi.enums.SubmissionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public class StatisticServiceTest {
    @Autowired
    StatisticService service;
    @MockBean
    StatisticRepository repository;
    @MockBean
    StatisticMapper mapper;

    @Test
    void getById() {
        UUID expectedUuid = UUID.randomUUID();
        mockAuthentication(expectedUuid);
        StatisticEntity statisticEntity =
                new StatisticEntity(expectedUuid, 0, 0, 0, 0, 0, 0);
        StatisticResponse expectedResponse =
                new StatisticResponse(expectedUuid, 0, 0, 0, 0, 0, 0);

        when(repository.findById(expectedUuid)).thenReturn(Optional.of(statisticEntity));
        when(mapper.toResponse(statisticEntity)).thenReturn(expectedResponse);

        StatisticResponse response = service.getById();

        assertEquals(expectedResponse, response);
        verify(repository).findById(expectedUuid);
        verify(mapper).toResponse(statisticEntity);
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
        doNothing().when(repository).update(any(StatisticEntity.class));

        service.update(request);

        assertEquals(2, statisticEntity.getAttempts());
        assertEquals(2, statisticEntity.getSolvedTasks());
        assertEquals(1, statisticEntity.getEasy());
        assertEquals(100, statisticEntity.getSuccessPercentage());
        verify(repository).findById(userId);
        verify(repository).update(statisticEntity);
    }

    private void mockAuthentication(UUID userId) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId.toString());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}