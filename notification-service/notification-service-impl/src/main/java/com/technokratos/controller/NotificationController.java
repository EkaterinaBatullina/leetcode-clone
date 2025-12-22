package com.technokratos.controller;

import com.technokratos.api.NotificationApi;
import com.technokratos.dto.enams.Status;
import com.technokratos.dto.response.NotificationResponse;
import com.technokratos.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {
    private final NotificationService service;

    @Override
    public Page<NotificationResponse> getAllByStatus(Status status, Pageable pageable) {
        return service.getAllByStatus(status, pageable);
    }

    @Override
    public Page<NotificationResponse> getAllByUserId(UUID userId, Pageable pageable) {
        return service.getAllByUserId(userId, pageable);
    }
}
