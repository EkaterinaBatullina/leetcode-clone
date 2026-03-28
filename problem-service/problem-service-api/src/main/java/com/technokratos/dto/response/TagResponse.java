package com.technokratos.dto.response;

import java.util.UUID;

public record TagResponse(
        UUID id,
        String name,
        String description
) {}
