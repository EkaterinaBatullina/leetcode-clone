package com.technokratos.submissionserviceimpl.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMetadataService {
    private final StringRedisTemplate redis;

    public String getSubmissionIdFromToken(String token) {
        return redis.opsForValue().get(RedisKeysUtil.tokenToSubmission(token));
    }

    public String getAction(String submissionId) {
        return redis.opsForValue().get(RedisKeysUtil.submissionAction(submissionId));
    }
}