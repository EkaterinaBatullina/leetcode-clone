package com.technokratos.service;

import com.technokratos.dto.enums.Status;

import java.util.UUID;

public interface OutboxService {

    void updateStatus(UUID id, Status status);

    void saveUserRegisteredOutboxEvent(UUID userId, String username, String email);
}
