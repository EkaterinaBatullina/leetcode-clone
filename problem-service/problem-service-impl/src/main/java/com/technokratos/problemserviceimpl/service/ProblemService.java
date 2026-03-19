package com.technokratos.problemserviceimpl.service;

import com.technokratos.problemserviceapi.dto.request.ProblemRequest;
import com.technokratos.problemserviceapi.dto.request.PublishProblemsRequest;
import com.technokratos.problemserviceapi.dto.request.PublishTestcasesRequest;
import com.technokratos.problemserviceapi.dto.request.RunRequest;
import com.technokratos.problemserviceapi.dto.response.ProblemResponse;
import com.technokratos.problemserviceapi.dto.response.TestcaseResponse;
import com.technokratos.problemserviceapi.dto.response.WrapperResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.problemserviceapi.enums.PublishStatus;
import com.technokratos.problemserviceimpl.entity.Problem;
import com.technokratos.problemserviceimpl.exception.ProblemNotFoundException;
import com.technokratos.problemserviceimpl.exception.PublishingFailedException;
import com.technokratos.problemserviceimpl.kafka.producer.KafkaProducerService;
import com.technokratos.problemserviceimpl.mapper.ProblemMapper;
import com.technokratos.problemserviceimpl.repository.ProblemRepository;
import com.technokratos.problemserviceimpl.service.base.BaseProblemService;
import com.technokratos.problemserviceimpl.specification.ProblemSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.technokratos.submissionserviceapi.enums.Action;

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
    private final WrapperService wrapperService;
    private final CodeWrappingService codeWrappingService;

    @Override
    @Cacheable(value = "problems", key = "#id")
    public ProblemResponse findById(UUID id) {
        Problem problem = repository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException(id));
        return mapper.toResponse(problem);
    }

    @Override
    @Cacheable(value = "problems")
    public List<ProblemResponse> getAll() {
        return mapper.toResponse(repository.findAll());
    }

    @Override
    public Page<ProblemResponse> getAllWithPagination(List<Difficulty> difficulties, List<String> tags, Pageable pageable) {
        Specification<Problem> spec = Specification.where(
                ProblemSpecifications.withDifficulties(difficulties)
        ).and(
                ProblemSpecifications.withAllTags(tags)
        );
        Page<Problem> problems = repository.findAll(spec, pageable);
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
                .orElseThrow(() -> new ProblemNotFoundException(id));
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
            List<TestcaseResponse> testcases = problem.testcases();
            PublishTestcasesRequest publishRequest = new PublishTestcasesRequest(problemId, problem.difficulty(), testcases);
            publishTestcasesRequests.add(publishRequest);
        }
        return new PublishProblemsRequest(UUID.randomUUID(), publishTestcasesRequests);
    }

    @Override
    public Difficulty getDifficulty(UUID id) {
        return repository.findDifficultyById(id)
                .orElseThrow(() -> new ProblemNotFoundException(id));
    }

    @Override
    public PublishStatus getPublishStatus(UUID id) {
        return repository.findPublishStatusById(id)
                .orElseThrow(() -> new ProblemNotFoundException(id));
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
        processRequest(request, Action.RUN);
    }

    @Override
    public void submit(RunRequest request) {
        processRequest(request, Action.SUBMIT);
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

    private void processRequest(RunRequest request, Action type) {
        try {
            if (isPublished(request.problemId())) {
                log.debug("sending without publishing: {}", request);
                dispatchRequest(request, type);
            } else {
                publishingCoordinatorService.publishWithAck(build(request.problemId()))
                        .thenAccept(status -> {
                            if (status.equals(PublishStatus.PUBLISHED)) {
                                dispatchRequest(request, type);
                            } else {
                                throw new PublishingFailedException(request.problemId());
                            }
                        });
            }
        } catch (ProblemNotFoundException ex) {
            log.error("problem not found during publishing: {}", request.problemId());
            throw ex;
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