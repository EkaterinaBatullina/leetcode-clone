package com.technokratos.submissionserviceapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Judge0BatchRequest(
        @JsonProperty("submissions") List<Judge0Request> submission
) {
}
