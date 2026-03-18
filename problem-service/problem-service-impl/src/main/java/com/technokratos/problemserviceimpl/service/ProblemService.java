package com.technokratos.problemserviceimpl.service;

import com.technokratos.problemserviceapi.dto.request.ProblemRequest;
import com.technokratos.problemserviceapi.dto.request.PublishProblemsRequest;
import com.technokratos.problemserviceapi.dto.request.PublishTestcasesRequest;
import com.technokratos.problemserviceapi.dto.request.RunRequest;
import com.technokratos.problemserviceapi.dto.response.ProblemResponse;
import com.technokratos.problemserviceapi.dto.response.TestcaseResponse;
<<<<<<< HEAD
=======
import com.technokratos.problemserviceapi.dto.response.WrapperResponse;
>>>>>>> feature/problem-and-submission-service
import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.problemserviceapi.enums.PublishStatus;
import com.technokratos.problemserviceimpl.entity.Problem;
import com.technokratos.problemserviceimpl.kafka.producer.KafkaProducerService;
import com.technokratos.problemserviceimpl.mapper.ProblemMapper;
import com.technokratos.problemserviceimpl.repository.ProblemRepository;
import com.technokratos.problemserviceimpl.service.base.BaseProblemService;
<<<<<<< HEAD
=======
import com.technokratos.problemserviceimpl.specification.ProblemSpecifications;
>>>>>>> feature/problem-and-submission-service
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
<<<<<<< HEAD
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
=======
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.technokratos.submissionserviceapi.enums.Action;
>>>>>>> feature/problem-and-submission-service

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemService implements BaseProblemService {
    private final ProblemRepository repository;
    private final ProblemMapper mapper;
    private final KafkaProducerService kafkaProducerService;
    private final PublishingCoordinatorService publishingCoordinatorService;
    private final TestcaseService testcaseService;
<<<<<<< HEAD
=======
    private final WrapperService wrapperService;
    private final CodeWrappingService codeWrappingService;
>>>>>>> feature/problem-and-submission-service

    @Override
    @Cacheable(value = "problems", key = "#id")
    public ProblemResponse findById(UUID id) {
        Problem problem = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("user not found with id: %s".formatted(id)));
        return mapper.toResponse(problem);
    }

    @Override
    @Cacheable(value = "problems")
    public List<ProblemResponse> getAll() {
        return mapper.toResponse(repository.findAll());
    }

    @Override
<<<<<<< HEAD
    public Page<ProblemResponse> getAllWithPagination(List<Difficulty> difficulty, List<String> category, List<String> tag, Pageable pageable) {
        List<Difficulty> difficulties = (difficulty != null && !difficulty.isEmpty()) ? difficulty : null;
        List<String> categories = (category != null && !category.isEmpty()) ? category : null;
        List<String> tags = (tag != null && !tag.isEmpty()) ? tag : null;

        Page<Problem> problems = repository.findByFiltersPaged(difficulties, categories, tags, pageable);
=======
    public Page<ProblemResponse> getAllWithPagination(List<Difficulty> difficulties, List<String> tags, Pageable pageable) {
        Specification<Problem> spec = Specification.where(
                ProblemSpecifications.withDifficulties(difficulties)
        ).and(
                ProblemSpecifications.withAllTags(tags)
        );
        Page<Problem> problems = repository.findAll(spec, pageable);
>>>>>>> feature/problem-and-submission-service
        return problems.map(mapper::toResponse);
    }

    @Override
    @CacheEvict(value = "problems", key = "#id")
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public UUID create(ProblemRequest problemRequest) {
        Problem problem = mapper.toEntity(problemRequest);
        problem = repository.save(problem);
        return problem.getId();
    }

    @Override
    @CacheEvict(value = "problems", key = "#id")
    public void replace(UUID id, ProblemRequest problemRequest) {
        Problem problem = mapper.toEntity(problemRequest);
        problem.setId(id);
        repository.save(problem);
    }

    @Override
    @CacheEvict(value = "problems", key = "#id")
    public void update(UUID id, ProblemRequest problemRequest) {
        Problem problem = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("problem not found with id: %s".formatted(id)));
        mapper.updateEntity(problemRequest, problem);
        repository.save(problem);
    }

    @Override
    public PublishProblemsRequest findAllByReadyForPublish() {
        List<ProblemResponse> problems = mapper.toResponse(repository.findAllByReadyForPublish());
        log.debug("here are not published problems: {}", problems);
        List<PublishTestcasesRequest> publishTestcasesRequests = new ArrayList<>();
        for (ProblemResponse problem : problems) {
            UUID problemId = problem.id();
<<<<<<< HEAD
            List<TestcaseResponse> testcases = problem.testcases()
                    .stream()
                    .map(tc -> new TestcaseResponse(
                            tc.id(),
                            tc.inputData(),
                            tc.expectedOutput(),
                            tc.visible(),
                            tc.cpuTimeLimit(),
                            tc.memoryLimit()
                    ))
                    .toList();
=======
            List<TestcaseResponse> testcases = problem.testcases();
>>>>>>> feature/problem-and-submission-service
            PublishTestcasesRequest publishRequest = new PublishTestcasesRequest(problemId, problem.difficulty(), testcases);
            publishTestcasesRequests.add(publishRequest);
        }
        return new PublishProblemsRequest(UUID.randomUUID(), publishTestcasesRequests);
    }

    @Override
    public Difficulty getDifficulty(UUID id) {
        return repository.findDifficultyById(id)
                .orElseThrow(() -> new IllegalStateException("problem not found or difficulty is null"));
    }

    @Override
    public PublishStatus getPublishStatus(UUID id) {
        return repository.findPublishStatusById(id)
                .orElseThrow(() -> new IllegalStateException("problem not found or published_status is null"));
    }

    @Override
    public void markAsFailed(UUID id) {
        repository.markAsFailed(id);
    }

    @Override
    public void markAsPublished(UUID id) {
        repository.markAsPublished(id);
    }

    @Override
    public List<Problem> getAllByReadyForPublish() {
        return repository.findAllByReadyForPublish();
    }

    @Override
    public void run(RunRequest request) {
<<<<<<< HEAD
        if (getPublishStatus(request.problemId()).equals(PublishStatus.PUBLISHED)) {
            kafkaProducerService.sendEventToRunTopic(request);
        } else {
            publishingCoordinatorService.publishWithAck(build(request.problemId()))
                    .thenAccept(status -> {
                        if (status.equals(PublishStatus.PUBLISHED)) {
                            kafkaProducerService.sendEventToRunTopic(request);
                        } else {
                            throw new IllegalStateException("testcase publishing failed: " + status);
                        }
                    });
        }
=======
        processRequest(request, Action.RUN);
>>>>>>> feature/problem-and-submission-service
    }

    @Override
    public void submit(RunRequest request) {
<<<<<<< HEAD
        if (getPublishStatus(request.problemId()).equals(PublishStatus.PUBLISHED)) {
            kafkaProducerService.sendEventToSubmitTopic(request);
        } else {
            publishingCoordinatorService.publishWithAck(build(request.problemId()))
                    .thenAccept(status -> {
                        if (status.equals(PublishStatus.PUBLISHED)) {
                            kafkaProducerService.sendEventToSubmitTopic(request);
                        } else {
                            throw new IllegalStateException("testcase publishing failed: " + status);
                        }
                    });
        }

=======
        processRequest(request, Action.SUBMIT);
>>>>>>> feature/problem-and-submission-service
    }

    @Override
    public void publishProblems() {
        kafkaProducerService.sendEventToPublishProblemsTopic(findAllByReadyForPublish());
    }

    @Override
    public void publishTestcases(UUID problemId) {
        PublishTestcasesRequest request = build(problemId);
        log.debug("publishing this request: {}", request);
        kafkaProducerService.sendEventToPublishTestcasesTopic(request);
    }

    private PublishTestcasesRequest build(UUID problemId) {
        Difficulty difficulty = getDifficulty(problemId);
        List<TestcaseResponse> testcases = testcaseService.getAllByProblemId(problemId);
        return new PublishTestcasesRequest(problemId, difficulty, testcases);
    }
<<<<<<< HEAD
}
=======

    private void processRequest(RunRequest request, Action type) {
        if (isPublished(request.problemId())) {
            log.debug("sending without publishing: {}", request);
            dispatchRequest(request, type);
        } else {
            publishingCoordinatorService.publishWithAck(build(request.problemId()))
                    .thenAccept(status -> {
                        if (status.equals(PublishStatus.PUBLISHED)) {
                            dispatchRequest(request, type);
                        } else {
                            throw new IllegalStateException("testcase publishing failed: " + status);
                        }
                    });
        }
    }

    private void dispatchRequest(RunRequest request, Action type) {
        String wrapper = getWrapper(request.problemId(), request.languageId());
        RunRequest finalRequest = (wrapper != null) ? wrapCodeRequest(request, wrapper) : request;
        switch (type) {
            case RUN -> {
                if (wrapper != null) {
                    kafkaProducerService.sendEventToRunWithWrapperTopic(finalRequest);
                } else {
                    kafkaProducerService.sendEventToRunTopic(finalRequest);
                }
            }
            case SUBMIT -> {
                if (wrapper != null) {
                    kafkaProducerService.sendEventToSubmitWithWrapperTopic(finalRequest);
                } else {
                    kafkaProducerService.sendEventToSubmitTopic(finalRequest);
                }
            }
        }
    }

    private boolean isPublished(UUID problemId) {
        log.debug("this problem has status: {}", getPublishStatus(problemId));
        return getPublishStatus(problemId).equals(PublishStatus.PUBLISHED);
    }

    private String getWrapper(UUID problemId, int languageId) {
        return wrapperService.findByProblemIdAndLanguageId(problemId, languageId)
                .map(WrapperResponse::wrapper)
                .orElse(null);
    }

    private RunRequest wrapCodeRequest(RunRequest request, String wrapper) {
        String wrappedCode = codeWrappingService.wrapUserCode(request.sourceCode(), wrapper);
        return new RunRequest(
                request.id(),
                request.problemId(),
                request.userId(),
                wrappedCode,
                request.languageId()
        );
    }

}
>>>>>>> feature/problem-and-submission-service
