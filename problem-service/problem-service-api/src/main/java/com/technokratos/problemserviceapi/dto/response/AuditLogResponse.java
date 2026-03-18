package com.technokratos.problemserviceapi.dto.response;

import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        UUID problemId,
        UUID userId,
        String actionType,
        String oldData,
        String newData,
        String timestamp
) {}