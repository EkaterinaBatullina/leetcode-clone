package com.technokratos.service;

import com.technokratos.problemserviceapi.dto.request.RunRequest;
import com.technokratos.dto.request.Judge0BatchRequest;
import com.technokratos.dto.request.Judge0Request;
import com.technokratos.dto.request.SubmissionRequest;
import com.technokratos.enums.Action;
import com.technokratos.config.properties.Judge0Properties;
import com.technokratos.entity.Testcase;
import com.technokratos.feign.Judge0Client;
import com.fasterxml.jackson.databind.JsonNode;
import com.technokratos.redis.RedisKeysUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ListOperations;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Judge0ServiceTest {

    @Mock
    private Judge0Client judge0Client;
    @Mock
    private ProblemTestcasesService problemTestcasesService;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private ListOperations<String, String> listOperations;
    @Mock
    private SubmissionService submissionService;
    @Mock
    private Judge0Properties judge0Properties;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private Judge0Service judge0Service;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenReturn("dummy");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    @Test
    void sendBatchSubmission_Success() {
        // Setup
        UUID submissionId = UUID.randomUUID();
        String submissionIdStr = submissionId.toString();
        UUID problemId = UUID.randomUUID();
        RunRequest request = new RunRequest(submissionId, UUID.randomUUID(), problemId, "print('Hello')", 71);

        List<Testcase> testcases = List.of(
                new Testcase(UUID.randomUUID(), "input1", "output1", true, 100, 128),
                new Testcase(UUID.randomUUID(), "input2", "output2", false, 200, 256)
        );

        when(problemTestcasesService.getAllByProblemId(any(UUID.class))).thenReturn(testcases);
        when(judge0Properties.getBatchSize()).thenReturn(10);
        when(judge0Properties.getCallbackUrl()).thenReturn("http://callback");
        when(judge0Client.sendBatch(any(Judge0BatchRequest.class))).thenReturn(mock(JsonNode.class));

        // Execute
        judge0Service.sendBatchSubmission(request, Action.SUBMIT);

        // Verify
        verify(problemTestcasesService).getAllByProblemId(any(UUID.class));
        verify(judge0Client).sendBatch(any(Judge0BatchRequest.class));
        verify(valueOperations).set(
                eq(RedisKeysUtil.submissionAction(submissionIdStr)),
                eq("SUBMIT"),
                eq(Duration.ofMinutes(10))
        );
        verify(valueOperations).set(
                eq(RedisKeysUtil.submissionIsSingleRequest(submissionIdStr)),
                eq("true"),
                eq(Duration.ofMinutes(10))
        );
        verify(submissionService).create(any(SubmissionRequest.class));
    }

    @Test
    void sendBatch_Success() {
        // Setup
        UUID submissionId = UUID.randomUUID();
        String token = "judge0-token";
        List<Judge0Request> batch = List.of(
                new Judge0Request("code", 71, "input", "output", "callback", 1, 100)
        );

        JsonNode responseNode = mock(JsonNode.class);
        JsonNode tokenNode = mock(JsonNode.class);

        when(judge0Client.sendBatch(any(Judge0BatchRequest.class))).thenReturn(responseNode);
        when(responseNode.isArray()).thenReturn(true);
        when(responseNode.iterator()).thenReturn(List.of(tokenNode).iterator());
        when(tokenNode.get("token")).thenReturn(tokenNode);
        when(tokenNode.asText()).thenReturn("judge0-token");

        // Execute
        judge0Service.sendBatch(batch, submissionId);

        // Verify
        verify(valueOperations).set(
                eq(RedisKeysUtil.tokenToSubmission(token)),
                eq(submissionId.toString()),
                eq(Duration.ofMinutes(10))
        );
        verify(listOperations).rightPushAll(
                eq(RedisKeysUtil.submissionTokens(submissionId.toString())),
                eq(List.of(token))
        );

    }
}