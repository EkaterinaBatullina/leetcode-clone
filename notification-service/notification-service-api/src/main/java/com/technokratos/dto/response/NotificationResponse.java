package com.technokratos.dto.response;

import com.technokratos.dto.enums.Status;

import java.time.Instant;

public record NotificationResponse (
       String username, String email, Status status, Instant createdAt
) {}
