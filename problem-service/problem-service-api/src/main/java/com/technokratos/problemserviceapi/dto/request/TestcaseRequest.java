package com.technokratos.problemserviceapi.dto.request;

public record TestcaseRequest(
        String inputData,
        String expectedOutput,
        int timeout,
        boolean visible,
        int cpuTimeLimit,
        int memoryLimit
) {}