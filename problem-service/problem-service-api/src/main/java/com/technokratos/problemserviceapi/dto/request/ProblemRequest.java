package com.technokratos.problemserviceapi.dto.request;


import com.technokratos.problemserviceapi.dto.response.TagResponse;
import com.technokratos.problemserviceapi.dto.response.TestcaseResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;

import java.util.List;

public record ProblemRequest(
        String title,
        String description,
        String constraints,
        Difficulty difficulty,
        List<TestcaseResponse> testcaseResponses,
        List<TagResponse> tags
) {
}

