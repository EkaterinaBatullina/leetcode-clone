package com.technokratos.service.base;


import com.technokratos.dto.request.SubmissionRequest;
import com.technokratos.dto.response.SubmissionResponse;

import java.util.List;
import java.util.UUID;

public interface BaseSubmissionService {
    SubmissionResponse findById(String id);

    List<SubmissionResponse> findAllByUserId(UUID userId);

    void deleteById(String id);

    String create(SubmissionRequest submissionRequest);

    void update(String id, SubmissionRequest submissionRequest);

    boolean isFirstSuccessfulAttempt(UUID userId, UUID problemId);
}
