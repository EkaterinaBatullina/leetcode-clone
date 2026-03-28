package com.technokratos.dto.response;

import com.technokratos.enums.PublishStatus;

import java.util.UUID;

public record PublishTestcasesResponse(
        UUID problemId,
        PublishStatus status
) {
}
