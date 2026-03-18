package com.technokratos.problemserviceapi.dto.response;

import com.technokratos.problemserviceapi.enums.Difficulty;

import java.util.List;

public record MetadataResponse(
        List<Difficulty> difficulties,
        List<TagResponse> tags
) {}
