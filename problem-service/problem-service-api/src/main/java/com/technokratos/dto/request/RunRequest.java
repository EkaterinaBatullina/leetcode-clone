package com.technokratos.dto.request;

import java.util.UUID;

public record RunRequest(
        UUID id,
        UUID problemId,
        UUID userId,
        String sourceCode,
        int languageId
) {
}
