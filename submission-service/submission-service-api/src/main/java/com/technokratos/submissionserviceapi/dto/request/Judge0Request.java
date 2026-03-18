package com.technokratos.submissionserviceapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Judge0Request(
        @JsonProperty("source_code") String sourceCode,
        @JsonProperty("language_id") int languageId,
        @JsonProperty("stdin") String stdin,
        @JsonProperty("expected_output") String expectedOutput,
        @JsonProperty("callback_url") String callbackUrl,
        @JsonProperty("cpu_time_limit") Integer cpuTimeLimit,
        @JsonProperty("memory_limit") Integer memoryLimit
) {}
