package com.technokratos.problemserviceimpl.service;

import com.technokratos.problemserviceapi.dto.request.ProblemRequest;
import com.technokratos.problemserviceapi.dto.request.PublishProblemsRequest;
import com.technokratos.problemserviceapi.dto.request.RunRequest;
import com.technokratos.problemserviceapi.dto.response.ProblemResponse;
import com.technokratos.problemserviceapi.dto.response.TestcaseResponse;
import com.technokratos.problemserviceapi.dto.response.WrapperResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.problemserviceapi.enums.PublishStatus;
import com.technokratos.problemserviceimpl.BaseServiceTest;
import com.technokratos.problemserviceimpl.entity.Problem;
import com.technokratos.problemserviceimpl.entity.Testcase;
import com.technokratos.problemserviceimpl.mapper.ProblemMapper;
import com.technokratos.problemserviceimpl.mapper.TestcaseMapper;
import com.technokratos.problemserviceimpl.repository.ProblemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ProblemServiceTest extends BaseServiceTest {
    @Autowired
    private ProblemService problemService;

    @MockitoBean
    private ProblemRepository problemRepository;

    @MockitoBean
    private ProblemMapper problemMapper;

    @MockitoBean
    private PublishingCoordinatorService publishingCoordinatorService;

    @MockitoBean
    private TestcaseService testcaseService;

    @MockitoBean
    private WrapperService wrapperService;

    @MockitoBean
    private CodeWrappingService codeWrappingService;

    @Autowired
    private ProblemMapper mapper;
    @MockitoBean
    private TestcaseMapper testcaseMapper;

    @Test
    void run_whenProblemNotPublished_thenPublishesAndSends() {
        RunRequest request = new RunRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "code", 1
        );

        when(problemRepository.findDifficultyById(any(UUID.class)))
                .thenReturn(Optional.of(Difficulty.MEDIUM));
        when(testcaseService.getAllByProblemId(any(UUID.class)))
                .thenReturn(List.of());

        when(problemRepository.findPublishStatusById(request.problemId()))
                .thenReturn(Optional.of(PublishStatus.NOT_PUBLISHED));

        // Mock successful publishing
        when(publishingCoordinatorService.publishWithAck(any()))
                .thenReturn(CompletableFuture.completedFuture(PublishStatus.PUBLISHED));

        problemService.run(request);

        verify(publishingCoordinatorService).publishWithAck(any());
        verify(kafkaProducerService).sendEventToRunTopic(request);
    }

    @Test
    void submit_whenPublishingFails_thenThrowsException() {
        RunRequest request = new RunRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "code", 1
        );

        when(problemRepository.findPublishStatusById(request.problemId()))
                .thenReturn(Optional.of(PublishStatus.NOT_PUBLISHED));

        when(publishingCoordinatorService.publishWithAck(any()))
                .thenReturn(CompletableFuture.completedFuture(PublishStatus.FAILED));

        assertThrows(IllegalStateException.class, () ->
                problemService.submit(request)
        );
    }

    @Test
    void run_withWrapperCode_thenSendsWrappedCode() {
        RunRequest request = new RunRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "user_code", 1
        );
        String wrapper = "def wrapper(user_code): ...";

        when(problemRepository.findPublishStatusById(request.problemId()))
                .thenReturn(Optional.of(PublishStatus.PUBLISHED));
        when(wrapperService.findByProblemIdAndLanguageId(any(UUID.class), anyInt()))
                .thenReturn(Optional.of(new WrapperResponse("signature", wrapper)));
        when(codeWrappingService.wrapUserCode(anyString(), anyString()))
                .thenReturn("wrapped_code");

        problemService.run(request);

        ArgumentCaptor<RunRequest> captor = ArgumentCaptor.forClass(RunRequest.class);
        verify(kafkaProducerService).sendEventToRunWithWrapperTopic(captor.capture());

        assertEquals("wrapped_code", captor.getValue().sourceCode());
    }

    @Test
    void publishTestcases_whenProblemNotFound_thenThrowsException() {
        UUID problemId = UUID.randomUUID();

        when(problemRepository.findDifficultyById(problemId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                problemService.publishTestcases(problemId)
        );
    }

    @Test
    void findAllByReadyForPublish_withTestcases_thenBuildsRequest() {
        // Create problem with testcases
        Problem problem = new Problem();
        UUID problemId = UUID.randomUUID();
        problem.setId(problemId);
        problem.setTitle("Test Problem");
        problem.setDescription("Test Description");
        problem.setConstraints("Test Constraints");
        problem.setDifficulty(Difficulty.HARD);
        problem.setReadyForPublish(true);

        // Create and associate testcases
        Testcase testcaseEntity = new Testcase();
        testcaseEntity.setId(UUID.randomUUID());
        testcaseEntity.setInputData("in");
        testcaseEntity.setExpectedOutput("out");
        testcaseEntity.setVisible(true);
        testcaseEntity.setCpuTimeLimit(1000);
        testcaseEntity.setMemoryLimit(256);
        testcaseEntity.setProblem(problem);

        // Add testcase to problem's testcases list
        problem.setTestcases(List.of(testcaseEntity));

        // Mock repository response
        when(problemRepository.findAllByReadyForPublish()).thenReturn(List.of(problem));

        // Mock mapper behavior
        TestcaseResponse testcaseResponse = new TestcaseResponse(
                testcaseEntity.getId(), "in", "out", true, 1000, 256
        );
        when(testcaseMapper.toResponse(List.of(testcaseEntity))).thenReturn(List.of(testcaseResponse));

        ProblemResponse problemResponse = new ProblemResponse(
                problemId,
                problem.getTitle(),
                problem.getDescription(),
                problem.getConstraints(),
                problem.getDifficulty(),
                List.of(testcaseResponse),
                null
        );

        when(problemMapper.toResponse(List.of(problem))).thenReturn(List.of(problemResponse));

        PublishProblemsRequest result = problemService.findAllByReadyForPublish();

        assertEquals(1, result.publishTestcasesRequestList().size());
        assertEquals(problemId, result.publishTestcasesRequestList().get(0).problemId());
        assertEquals(1, result.publishTestcasesRequestList().get(0).testcases().size());
    }

    @Test
    @CacheEvict(value = "problems", key = "#id")
    public void update_evictsCache() {
        UUID id = UUID.randomUUID();
        ProblemRequest request = new ProblemRequest("Title", "Desc", "Constraints",
                Difficulty.EASY, List.of(), List.of());

        Problem problem = new Problem();
        when(problemRepository.findById(id)).thenReturn(Optional.of(problem));

        problemService.update(id, request);

        verify(mapper).updateEntity(request, problem);
        verify(problemRepository).save(problem);
    }

}