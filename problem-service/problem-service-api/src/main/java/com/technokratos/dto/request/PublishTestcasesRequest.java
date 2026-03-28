package com.technokratos.dto.request;

import com.technokratos.dto.response.TestcaseResponse;
import com.technokratos.enums.Difficulty;

import java.util.List;
import java.util.UUID;

public record PublishTestcasesRequest(
        UUID problemId,
        Difficulty difficulty,
        List<TestcaseResponse> testcases
) {
}
