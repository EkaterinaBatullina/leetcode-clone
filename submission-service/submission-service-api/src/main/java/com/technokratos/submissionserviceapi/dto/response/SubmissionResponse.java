package com.technokratos.submissionserviceapi.dto.response;

import com.technokratos.submissionserviceapi.enums.SubmissionStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubmissionResponse(
        UUID id,
        UUID userId,
        UUID problemId,
        int languageId,
        String sourceCode,
        SubmissionStatus status,
        Instant createdAt,
        List<Judge0Response> responses
) {
}
