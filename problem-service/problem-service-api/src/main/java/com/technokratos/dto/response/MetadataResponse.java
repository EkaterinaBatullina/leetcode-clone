package com.technokratos.dto.response;

import com.technokratos.enums.Difficulty;

import java.util.List;

public record MetadataResponse(
        List<Difficulty> difficulties,
        List<TagResponse> tags
) {}
