package com.technokratos.controller;

import com.technokratos.api.external.ExternalProblemApi;
import com.technokratos.dto.request.RunRequest;
import com.technokratos.dto.response.ProblemResponse;
import com.technokratos.enums.Difficulty;
import com.technokratos.service.ProblemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Slf4j
public class ProblemController implements ExternalProblemApi {
    private final ProblemService service;

    @Override
    public void run(RunRequest request) {
        log.debug("{}", request);
        service.run(request);
    }

    @Override
    public void submit(RunRequest request) {
        service.submit(request);
    }

    @Override
    public ProblemResponse findById(UUID id) {
        return service.findById(id);
    }

    @Override
    public List<ProblemResponse> getAll() {
        return service.getAll();
    }

    @Override
    public Page<ProblemResponse> getAllWithPagination(List<Difficulty> list, List<String> list1, List<String> list2, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProblemResponse> getAllWithPagination(List<Difficulty> difficulty, List<String> tag, Pageable pageable) {
        Page<ProblemResponse> pages = service.getAllWithPagination(difficulty, tag, pageable);
        log.debug("{}", pages);
        return pages;
    }
}
