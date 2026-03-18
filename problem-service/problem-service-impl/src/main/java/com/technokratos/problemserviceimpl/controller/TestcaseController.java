package com.technokratos.problemserviceimpl.controller;

import com.technokratos.problemserviceapi.api.internal.TestcaseApi;
import com.technokratos.problemserviceapi.dto.request.TestcaseRequest;
import com.technokratos.problemserviceapi.dto.response.TestcaseResponse;
import com.technokratos.problemserviceimpl.service.TestcaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestcaseController implements TestcaseApi {

    private final TestcaseService service;

    @Override
    public TestcaseResponse findById(UUID id) {
        return service.findById(id);
    }

    @Override
    public List<TestcaseResponse> getAllByProblemId(UUID problemId) {
        return service.getAllByProblemId(problemId);
    }

    @Override
    public void deleteById(UUID id) {
        service.deleteById(id);
    }

    @Override
    public UUID create(TestcaseRequest testcaseRequest) {
        return service.create(testcaseRequest);
    }

    @Override
    public void replace(UUID id, TestcaseRequest testcaseRequest) {
        service.replace(id, testcaseRequest);
    }

    @Override
    public void update(UUID id, TestcaseRequest testcaseRequest) {
        service.update(id, testcaseRequest);
    }

}
