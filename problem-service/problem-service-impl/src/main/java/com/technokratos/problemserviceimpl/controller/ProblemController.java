package com.technokratos.problemserviceimpl.controller;

import com.technokratos.problemserviceapi.api.external.ExternalProblemApi;
<<<<<<< HEAD
=======
import com.technokratos.problemserviceapi.dto.request.RunRequest;
>>>>>>> feature/problem-and-submission-service
import com.technokratos.problemserviceapi.dto.response.ProblemResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.problemserviceimpl.service.ProblemService;
import lombok.AllArgsConstructor;
<<<<<<< HEAD
=======
import lombok.extern.slf4j.Slf4j;
>>>>>>> feature/problem-and-submission-service
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
<<<<<<< HEAD
=======
@Slf4j
>>>>>>> feature/problem-and-submission-service
public class ProblemController implements ExternalProblemApi {
    private final ProblemService service;

    @Override
<<<<<<< HEAD
=======
    public void run(RunRequest request) {
        log.debug("{}", request);
        service.run(request);
    }

    @Override
    public void submit(RunRequest request) {
        service.submit(request);
    }

    @Override
>>>>>>> feature/problem-and-submission-service
    public ProblemResponse findById(UUID id) {
        return service.findById(id);
    }

    @Override
    public List<ProblemResponse> getAll() {
        return service.getAll();
    }

    @Override
<<<<<<< HEAD
    public Page<ProblemResponse> getAllWithPagination(List<Difficulty> difficulty, List<String> category, List<String> tag, Pageable pageable) {
        return service.getAllWithPagination(difficulty, category, tag, pageable);
=======
    public Page<ProblemResponse> getAllWithPagination(List<Difficulty> difficulty, List<String> tag, Pageable pageable) {
        Page<ProblemResponse> pages = service.getAllWithPagination(difficulty, tag, pageable);
        log.debug("{}", pages);
        return pages;
>>>>>>> feature/problem-and-submission-service
    }
}
