package com.technokratos.event;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID eventId, UUID userId, String username, String email
) {}
