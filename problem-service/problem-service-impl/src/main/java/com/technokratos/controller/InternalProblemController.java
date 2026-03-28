package com.technokratos.controller;

import com.technokratos.api.internal.ProblemApi;
import com.technokratos.dto.request.ProblemRequest;
import com.technokratos.service.ProblemService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@Log4j2
public class InternalProblemController implements ProblemApi {
    private final ProblemService service;

    @Override
    public void deleteById(UUID id) {
        service.deleteById(id);
    }

    @Override
    public UUID create(ProblemRequest userRequest) {
        return service.create(userRequest);
    }

    @Override
    public void replace(UUID id, ProblemRequest userRequest) {
        service.replace(id, userRequest);
    }

    @Override
    public void update(UUID id, ProblemRequest userRequest) {
        service.update(id, userRequest);
    }

    @Override
    public void publishProblems() {
        service.publishProblems();
    }

    @Override
    public void publishTestcases(UUID problemId) {
        service.publishTestcases(problemId);
    }
}
