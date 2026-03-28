package com.technokratos.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Judge0Response(
        String token,
        String stdout,
        String stderr,
        @JsonProperty("compile_output") String compileOutput,
        String message,
        Status status,
        @JsonProperty("language_id") Integer languageId,
        Double time,
        Integer memory
) {
    public record Status(
            Integer id,
            String description
    ) {
    }
}
