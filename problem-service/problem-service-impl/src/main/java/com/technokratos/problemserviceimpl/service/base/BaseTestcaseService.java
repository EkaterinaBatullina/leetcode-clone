package com.technokratos.problemserviceimpl.service.base;

import com.technokratos.problemserviceapi.dto.request.TestcaseRequest;
import com.technokratos.problemserviceapi.dto.response.TestcaseResponse;

import java.util.List;
import java.util.UUID;

public interface BaseTestcaseService {
    TestcaseResponse findById(UUID id);

    void deleteById(UUID id);

    UUID create(TestcaseRequest testcaseRequest);

    void replace(UUID id, TestcaseRequest testcaseRequest);

    void update(UUID id, TestcaseRequest testcaseRequest);
    List<TestcaseResponse> getAllByProblemId(UUID problemId);
}
