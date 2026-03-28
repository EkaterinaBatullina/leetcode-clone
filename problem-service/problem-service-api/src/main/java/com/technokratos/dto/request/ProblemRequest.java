package com.technokratos.dto.request;


import com.technokratos.dto.response.TagResponse;
import com.technokratos.dto.response.TestcaseResponse;
import com.technokratos.enums.Difficulty;

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

