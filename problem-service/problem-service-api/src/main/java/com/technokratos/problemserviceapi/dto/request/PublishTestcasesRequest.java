package com.technokratos.problemserviceapi.dto.request;

import com.technokratos.problemserviceapi.dto.response.TestcaseResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;

import java.util.List;
import java.util.UUID;

public record PublishTestcasesRequest(
        UUID problemId,
        Difficulty difficulty,
        List<TestcaseResponse> testcases
) {
}
