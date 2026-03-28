package com.technokratos.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.technokratos.redis.RedisKeysUtil.*;

@Service
@AllArgsConstructor
public class RedisCleanupService {

    private final RedisTemplate<String, String> redisTemplate;

    public void clearSubmission(String submissionId) {
        redisTemplate.delete(submissionTokens(submissionId));
        redisTemplate.delete(submissionAction(submissionId));
        redisTemplate.delete(submissionResponses(submissionId));
        redisTemplate.opsForList().remove("submission:queue", 1, submissionId);
    }

    public void clearTokenMappings(List<String> tokens) {
        for (String token : tokens) {
            redisTemplate.delete(tokenToSubmission(token));
        }
    }

    public void clearTokenMappingsBySubmissionId(String submissionId) {
        String tokenListKey = submissionTokens(submissionId);
        List<String> tokens = redisTemplate.opsForList().range(tokenListKey, 0, -1);
        if (tokens != null && !tokens.isEmpty()) {
            for (String token : tokens) {
                redisTemplate.delete(tokenToSubmission(token));
            }
        }
    }

    public void clearAllSubmissionKeys() {
        Set<String> keys = redisTemplate.keys("submission:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void clearAllTokenKeys() {
        Set<String> keys = redisTemplate.keys("token:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}

