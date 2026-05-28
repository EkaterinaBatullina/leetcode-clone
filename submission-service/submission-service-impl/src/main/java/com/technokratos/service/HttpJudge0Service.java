package com.technokratos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.dto.request.Judge0BatchRequest;
import com.technokratos.dto.request.Judge0Request;
import com.technokratos.dto.request.SubmissionRequest;
import com.technokratos.enums.Action;
import com.technokratos.enums.SubmissionStatus;
import com.technokratos.config.properties.RapidApiProperties;
import com.technokratos.entity.Testcase;
import com.technokratos.feign.RapidApiJudge0Client;
import com.technokratos.redis.RedisKeysUtil;
import com.technokratos.service.base.BaseJudge0Service;
import com.technokratos.dto.request.RunRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.technokratos.redis.RedisKeysUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HttpJudge0Service implements BaseJudge0Service {
    private final RapidApiJudge0Client client;
    private final RapidApiProperties properties;
    private final ProblemTestcasesService problemTestcasesService;
    private final RedisTemplate<String, String> redisTemplate;
    private final SubmissionService submissionService;
    private final ObjectMapper objectMapper;

    @Override
    public void sendBatchSubmission(RunRequest request, Action action) {
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
            sendBatch(batch, request.id());
        }
        redisTemplate.opsForValue().set(submissionAction(request.id().toString()), String.valueOf(action));
        SubmissionRequest submissionRequest = new SubmissionRequest(
                request.id(),
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
    public void sendSubmission(RunRequest request, Action action) {

    }

    @Async
    public void sendBatch(List<Judge0Request> batch, UUID submissionId) {
        try {
            log.debug("sending batch: {}", objectMapper.writeValueAsString(new Judge0BatchRequest(batch)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JsonNode response = client.sendBatch(properties.getApiKey(), properties.getApiHost(), new Judge0BatchRequest(batch));
        List<String> tokens = new ArrayList<>();
        if (response.isArray()) {
            for (JsonNode item : response) {
                JsonNode token = item.get("token");
                if (token != null) {
                    tokens.add(token.asText());
                    redisTemplate.opsForValue().set(RedisKeysUtil.tokenToSubmission(token.asText()), submissionId.toString());
                }
            }
        }
        log.debug("extracted tokens: {}", tokens);
        redisTemplate.opsForList().rightPushAll(RedisKeysUtil.submissionTokens(submissionId.toString()), tokens);
    }
}