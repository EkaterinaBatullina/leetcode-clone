package com.technokratos.dto.response;


import com.technokratos.enums.SubmissionStatus;

import java.util.List;
import java.util.UUID;

public record RunResponse(
        UUID id,
        SubmissionStatus status,
        List<Judge0Response> responses
) {
}
