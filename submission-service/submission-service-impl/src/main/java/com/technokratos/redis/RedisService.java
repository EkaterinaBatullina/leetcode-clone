package com.technokratos.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.dto.response.Judge0Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public void storeResponse(String submissionId, Judge0Response response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            redis.opsForList().rightPush(RedisKeysUtil.submissionResponses(submissionId), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error serializing Judge0Response", e);
        }
    }

    public boolean allResponsesReceived(String submissionId) {
        List<String> expectedTokens = redis.opsForList().range(RedisKeysUtil.submissionTokens(submissionId), 0, -1);
        Long receivedCount = redis.opsForList().size(RedisKeysUtil.submissionResponses(submissionId));
        log.debug("expected tokens: {}, received count: {}", expectedTokens, receivedCount);
        return expectedTokens != null && receivedCount != null && receivedCount.equals((long) expectedTokens.size());
    }

    public List<Judge0Response> getAllResponses(String submissionId) {
        List<String> responseJsons = redis.opsForList().range(RedisKeysUtil.submissionResponses(submissionId), 0, -1);
        assert responseJsons != null;
        return responseJsons.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, Judge0Response.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    public Boolean isSingleRequest(String submissionId) {
        return redis.hasKey(RedisKeysUtil.submissionIsSingleRequest(submissionId));
    }

    public String getSubmissionIdFromToken(String token) {
        return redis.opsForValue().get(RedisKeysUtil.tokenToSubmission(token));
    }

    public String getAction(String submissionId) {
        return redis.opsForValue().get(RedisKeysUtil.submissionAction(submissionId));
    }
}