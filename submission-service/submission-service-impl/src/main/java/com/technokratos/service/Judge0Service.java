package com.technokratos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.dto.request.Judge0BatchRequest;
import com.technokratos.dto.request.Judge0Request;
import com.technokratos.dto.request.SubmissionRequest;
import com.technokratos.enums.Action;
import com.technokratos.enums.SubmissionStatus;
import com.technokratos.feign.Judge0Client;
import com.technokratos.config.properties.Judge0Properties;
import com.technokratos.entity.Testcase;
import com.technokratos.redis.RedisKeysUtil;
import com.technokratos.service.base.BaseJudge0Service;
import com.technokratos.problemserviceapi.dto.request.RunRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.technokratos.redis.RedisKeysUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class Judge0Service implements BaseJudge0Service {
    private final Judge0Client client;
    private final Judge0Properties properties;
    private final ProblemTestcasesService problemTestcasesService;
    private final RedisTemplate<String, String> redisTemplate;
    private final SubmissionService submissionService;
    private final ObjectMapper objectMapper;

    @Override
    public void sendSubmission(RunRequest request, Action action) {
        UUID problemId = request.problemId();
        UUID submissionId = request.id();
        String inputs = (action == Action.RUN)
                ? problemTestcasesService.getVisibleInputs(problemId)
                : problemTestcasesService.getInputs(problemId);
        String outputs = (action == Action.RUN)
                ? problemTestcasesService.getVisibleOutputs(problemId)
                : problemTestcasesService.getOutputs(problemId);
        int cpuTimeLimit = (action == Action.RUN)
                ? problemTestcasesService.getVisibleCpuTimeLimit(problemId)
                : problemTestcasesService.getCpuTimeLimit(problemId);
        int memoryLimit = (action == Action.RUN)
                ? problemTestcasesService.getVisibleMemoryLimit(problemId)
                : problemTestcasesService.getMemoryLimit(problemId);
        Judge0Request submission = new Judge0Request(
                request.sourceCode(),
                request.languageId(),
                inputs,
                outputs,
                properties.getCallbackUrl(),
                cpuTimeLimit,
                memoryLimit
        );
        sendBatch(List.of(submission),submissionId);
        redisTemplate.opsForValue().set(submissionAction(submissionId.toString()), String.valueOf(action), Duration.ofMinutes(10));
        SubmissionRequest submissionRequest = new SubmissionRequest(
                submissionId,
                request.userId(),
                request.problemId(),
                request.languageId(),
                request.sourceCode(),
                SubmissionStatus.PENDING,
                Instant.now(),
                new ArrayList<>()
        );
        submissionService.create(submissionRequest);
    }

    @Override
    public void sendBatchSubmission(RunRequest request, Action action) {
        UUID submissionId = request.id();
        log.debug("received run request with id: {} and action: {}", submissionId, action);
        List<Testcase> testcases = (action == Action.RUN)
                ? problemTestcasesService.getAllVisibleByProblemId(request.problemId())
                : problemTestcasesService.getAllByProblemId(request.problemId());
        log.debug("testcases: {}", testcases);
        List<Judge0Request> submissions = testcases.stream().map(testcase -> new Judge0Request(
                request.sourceCode(),
                request.languageId(),
                testcase.getInputData(),
                testcase.getExpectedOutput(),
                properties.getCallbackUrl(),
                testcase.getCpuTimeLimit(),
                testcase.getMemoryLimit()
        )).toList();
        List<List<Judge0Request>> batches = ListUtils.partition(submissions, properties.getBatchSize());
        for (List<Judge0Request> batch : batches) {
            sendBatch(batch,submissionId);
        }
        redisTemplate.opsForValue().set(submissionAction(request.id().toString()), String.valueOf(action), Duration.ofMinutes(10));
        SubmissionRequest submissionRequest = new SubmissionRequest(
                submissionId,
                request.userId(),
                request.problemId(),
                request.languageId(),
                request.sourceCode(),
                SubmissionStatus.PENDING,
                Instant.now(),
                new ArrayList<>()
        );
        submissionService.create(submissionRequest);
        redisTemplate.opsForValue().set(RedisKeysUtil.submissionIsSingleRequest(request.id().toString()), "true", Duration.ofMinutes(10));
    }

    @Async
    public void sendBatch(List<Judge0Request> batch, UUID submissionId) {
        try {
            log.debug("sending batch: {}", objectMapper.writeValueAsString(new Judge0BatchRequest(batch)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JsonNode response = client.sendBatch(new Judge0BatchRequest(batch));
        List<String> tokens = new ArrayList<>();
        if (response.isArray()) {
            for (JsonNode item : response) {
                JsonNode token = item.get("token");
                if (token != null) {
                    tokens.add(token.asText());
                    redisTemplate.opsForValue().set(RedisKeysUtil.tokenToSubmission(token.asText()), submissionId.toString(), Duration.ofMinutes(10));
                }
            }
        }
        log.debug("extracted tokens: {}", tokens);
        redisTemplate.opsForList().rightPushAll(RedisKeysUtil.submissionTokens(submissionId.toString()), tokens);
    }
}