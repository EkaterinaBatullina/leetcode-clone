package com.technokratos.dto.response;

import com.technokratos.enums.Difficulty;

import java.util.List;
import java.util.UUID;

public record ProblemResponse(
        UUID id,
        String title,
        String description,
        String constraints,
        Difficulty difficulty,
        List<TestcaseResponse> testcases,
        List<TagResponse> tags
) {}