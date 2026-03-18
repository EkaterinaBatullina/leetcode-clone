package com.technokratos.problemserviceimpl.controller;

import com.technokratos.problemserviceapi.api.internal.ProblemApi;
import com.technokratos.problemserviceapi.dto.request.ProblemRequest;
<<<<<<< HEAD
import com.technokratos.problemserviceapi.dto.request.RunRequest;
=======
>>>>>>> feature/problem-and-submission-service
import com.technokratos.problemserviceimpl.service.ProblemService;
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
<<<<<<< HEAD
    public void run(RunRequest request) {
        service.run(request);
    }

    @Override
    public void submit(RunRequest request) {
        service.submit(request);
    }

    @Override
=======
>>>>>>> feature/problem-and-submission-service
    public void publishProblems() {
        service.publishProblems();
    }

    @Override
    public void publishTestcases(UUID problemId) {
        service.publishTestcases(problemId);
    }
}
