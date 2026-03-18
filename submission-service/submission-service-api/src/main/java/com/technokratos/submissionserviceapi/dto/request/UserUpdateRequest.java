package com.technokratos.submissionserviceapi.dto.request;

import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.submissionserviceapi.enums.SubmissionStatus;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        Difficulty difficulty,
        SubmissionStatus status,
        Boolean isFirstSuccessfulAttempt
) {
}