package com.technokratos.submissionserviceimpl.assembler;

import com.technokratos.submissionserviceapi.dto.request.SubmissionRequest;
import com.technokratos.submissionserviceapi.dto.response.Judge0Response;
import com.technokratos.submissionserviceapi.dto.response.SubmissionResponse;
import com.technokratos.submissionserviceapi.enums.SubmissionStatus;
import com.technokratos.submissionserviceimpl.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionAssembler {
    private final SubmissionService submissionService;

    public SubmissionRequest build(String submissionId, List<Judge0Response> responses) {
        SubmissionResponse existing = submissionService.findById(submissionId);
        SubmissionStatus status = responses.stream()
                .allMatch(r -> r.status() != null && r.status().id() == 3)
                ? SubmissionStatus.SOLVED
                : SubmissionStatus.ATTEMPTED;
        return new SubmissionRequest(
                existing.id(), existing.userId(), existing.problemId(),
                existing.languageId(), existing.sourceCode(), status,
                existing.createdAt(), responses
        );
    }
}

