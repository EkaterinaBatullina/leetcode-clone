package com.technokratos.problemserviceapi.dto.response;

import com.technokratos.problemserviceapi.enums.PublishStatus;

import java.util.UUID;

public record PublishTestcasesResponse(
        UUID problemId,
        PublishStatus status
) {
}
