package com.technokratos.problemserviceapi.dto.response;

import java.util.UUID;

public record TagResponse(
        UUID id,
        String name,
        String description
) {}
