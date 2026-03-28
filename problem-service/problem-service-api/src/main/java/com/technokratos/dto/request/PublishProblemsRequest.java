package com.technokratos.dto.request;

import java.util.List;
import java.util.UUID;

public record PublishProblemsRequest(
        UUID id,
        List<PublishTestcasesRequest> publishTestcasesRequestList
) {
}
