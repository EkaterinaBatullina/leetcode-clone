package com.technokratos.submissionserviceimpl.service;

import com.technokratos.problemserviceapi.dto.request.PublishTestcasesRequest;
import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.submissionserviceimpl.entity.ProblemDifficulty;
import com.technokratos.submissionserviceimpl.entity.ProblemTestcases;
import com.technokratos.submissionserviceimpl.entity.Testcase;
import com.technokratos.submissionserviceimpl.mapper.ProblemTestcasesMapper;
import com.technokratos.submissionserviceimpl.repository.ProblemTestcasesRepository;
import com.technokratos.submissionserviceimpl.service.base.BaseProblemTestcasesService;
<<<<<<< HEAD
=======
import com.technokratos.submissionserviceimpl.util.ResourceLimitCalculator;
>>>>>>> feature/problem-and-submission-service
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
<<<<<<< HEAD
import java.util.UUID;
=======
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
>>>>>>> feature/problem-and-submission-service

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemTestcasesService implements BaseProblemTestcasesService {
    private final ProblemTestcasesRepository repository;
    private final ProblemTestcasesMapper mapper;

    @Override
<<<<<<< HEAD
    @Cacheable(value = "testcases", key = "#problemId")
=======
    @Cacheable(value = "testcases", key = "T(String).format('%s_all_testcases', #problemId)")
>>>>>>> feature/problem-and-submission-service
    public List<Testcase> getAllByProblemId(UUID problemId) {
        log.debug("taking out all testcases for problem: {}", problemId);
        return repository.findById(problemId).getTestcases();
    }

    @Override
<<<<<<< HEAD
    @Cacheable(value = "visible-testcases", key = "#problemId")
=======
    @Cacheable(value = "testcases", key = "T(String).format('%s_visible_testcases', #problemId)")
>>>>>>> feature/problem-and-submission-service
    public List<Testcase> getAllVisibleByProblemId(UUID problemId) {
        log.debug("taking out visible testcases for problem: {}", problemId);
        return repository.findById(problemId).getTestcases().stream().filter(Testcase::isVisible).toList();
    }

    @Override
<<<<<<< HEAD
    //@Cacheable(value = "difficulty", key = "#problemId")
=======
    @Cacheable(value = "info", key = "T(String).format('%s_difficulty', #problemId)")
>>>>>>> feature/problem-and-submission-service
    public Difficulty getProblemDifficulty(UUID problemId) {
        return repository.findProblemDifficultyById(problemId)
                .map(ProblemDifficulty::getDifficulty)
                .orElseThrow(() -> new EntityNotFoundException("problem not found: " + problemId));
    }

    @Override
<<<<<<< HEAD
=======
    @Cacheable(value = "info", key = "T(String).format('%s_inputs', #problemId)")
    public String getInputs(UUID problemId) {
        return repository.findIOAndLimitsById(problemId).getInputs();
    }

    @Override
    @Cacheable(value = "info", key = "T(String).format('%s_outputs', #problemId)")
    public String getOutputs(UUID problemId) {
        return repository.findIOAndLimitsById(problemId).getOutputs();
    }

    @Override
    @Cacheable(value = "info", key = "T(String).format('%s_visible_inputs', #problemId)")
    public String getVisibleInputs(UUID problemId) {
        return repository.findVisibleIOAndLimitsById(problemId).getVisibleInputs();
    }

    @Override
    @Cacheable(value = "info", key = "T(String).format('%s_visible_outputs', #problemId)")
    public String getVisibleOutputs(UUID problemId) {
        return repository.findVisibleIOAndLimitsById(problemId).getVisibleOutputs();
    }

    @Override
    @Cacheable(value = "info", key = "T(String).format('%s_cpu_time_limit', #problemId)")
    public Integer getCpuTimeLimit(UUID problemId) {
        return repository.findIOAndLimitsById(problemId).getCpuTimeLimit();
    }

    @Override
    @Cacheable(value = "info", key = "T(String).format('%s_memory_limit', #problemId)")
    public Integer getMemoryLimit(UUID problemId) {
        return repository.findIOAndLimitsById(problemId).getMemoryLimit();
    }

    @Override
    @Cacheable(value = "info", key = "T(String).format('%s_cpu_time_limit', #problemId)")
    public Integer getVisibleCpuTimeLimit(UUID problemId) {
        return repository.findVisibleIOAndLimitsById(problemId).getVisibleCpuTimeLimit();
    }

    @Override
    @Cacheable(value = "info", key = "T(String).format('%s_memory_limit', #problemId)")
    public Integer getVisibleMemoryLimit(UUID problemId) {
        return repository.findVisibleIOAndLimitsById(problemId).getVisibleMemoryLimit();
    }

    @Override
>>>>>>> feature/problem-and-submission-service
    @Transactional
    public UUID create(PublishTestcasesRequest publishTestcasesRequest) {
//        throw new RuntimeException("hiii i'm an exception");
        ProblemTestcases problemTestcases = new ProblemTestcases();
<<<<<<< HEAD
        problemTestcases.setId(publishTestcasesRequest.problemId());
        problemTestcases.setDifficulty(publishTestcasesRequest.difficulty());
        problemTestcases.setTestcases(mapper.toEntity(publishTestcasesRequest.testcases()));
=======
        List<Testcase> testcases = mapper.toEntity(publishTestcasesRequest.testcases());
        List<Testcase> visibleTestcases = testcases.stream().filter(Testcase::isVisible).toList();
        problemTestcases.setId(publishTestcasesRequest.problemId());
        problemTestcases.setDifficulty(publishTestcasesRequest.difficulty());
        problemTestcases.setTestcases(testcases);
        setConcatenatedIOFields(problemTestcases, testcases);
        ResourceLimitCalculator.Limits limits = ResourceLimitCalculator.compute(testcases);
        problemTestcases.setCpuTimeLimit(limits.cpuTimeLimit());
        problemTestcases.setMemoryLimit(limits.memoryLimit());
        ResourceLimitCalculator.Limits visibleLimits = ResourceLimitCalculator.compute(visibleTestcases);
        problemTestcases.setVisibleCpuTimeLimit(visibleLimits.cpuTimeLimit());
        problemTestcases.setVisibleMemoryLimit(visibleLimits.memoryLimit());
>>>>>>> feature/problem-and-submission-service
        log.debug("created: {}", problemTestcases);
        return repository.save(problemTestcases).getId();
    }

    @Transactional
    public List<UUID> saveAll(List<PublishTestcasesRequest> publishTestcasesRequests) {
        List<UUID> uuids = new ArrayList<>();
<<<<<<< HEAD
        for (PublishTestcasesRequest request: publishTestcasesRequests) {
=======
        for (PublishTestcasesRequest request : publishTestcasesRequests) {
>>>>>>> feature/problem-and-submission-service
            uuids.add(create(request));
        }
        return uuids;
    }
<<<<<<< HEAD
=======

    private void setConcatenatedIOFields(ProblemTestcases target, List<Testcase> testcases) {
        target.setInputs(join(testcases, Testcase::getInputData));
        target.setOutputs(join(testcases, Testcase::getExpectedOutput));
        target.setVisibleInputs(join(testcases, t -> t.isVisible() ? t.getInputData() : null));
        target.setVisibleOutputs(join(testcases, t -> t.isVisible() ? t.getExpectedOutput() : null));
    }

    private String join(List<Testcase> testcases, Function<Testcase, String> extractor) {
        return testcases.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
    }
>>>>>>> feature/problem-and-submission-service
}
