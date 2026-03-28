package com.technokratos.dto.response;

import java.util.UUID;

public record TestcaseResponse(
        UUID id,
        String inputData,
        String expectedOutput,
        boolean visible,
        int cpuTimeLimit,
        int memoryLimit
) {}