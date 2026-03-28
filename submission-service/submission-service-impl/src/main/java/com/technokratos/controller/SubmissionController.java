package com.technokratos.controller;

import com.technokratos.api.internal.SubmissionApi;
import com.technokratos.dto.request.SubmissionRequest;
import com.technokratos.dto.response.SubmissionResponse;
import com.technokratos.service.SubmissionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class SubmissionController implements SubmissionApi {
    private final SubmissionService service;

    @Override
    public SubmissionResponse findById(String id) {
        return service.findById(id);
    }

    @Override
    public List<SubmissionResponse> findAllByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public void deleteById(String id) {

    }

    @Override
    public String create(SubmissionRequest submissionRequest) {
        return "";
    }

    @Override
    public void update(String id, SubmissionRequest submissionRequest) {

    }
}
