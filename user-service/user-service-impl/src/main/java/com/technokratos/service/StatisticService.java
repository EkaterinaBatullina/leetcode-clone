package com.technokratos.service;

import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.submissionserviceapi.dto.request.UserUpdateRequest;

import java.util.UUID;

public interface StatisticService {

    StatisticResponse getById();

    void create(UUID uuid);

    void update(UserUpdateRequest request);
}