package com.technokratos.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKeysUtil {

    public static String submissionTokens(String submissionId) {
        return "submission:%s:tokens".formatted(submissionId);
    }

    public static String submissionAction(String submissionId) {
        return "submission:%s:action".formatted(submissionId);
    }

    public static String submissionResponses(String submissionId) {
        return "submission:%s:responses".formatted(submissionId);
    }

    public static String tokenToSubmission(String token) {
        return "token:%s:submissionId".formatted(token);
    }

    public static String submissionIsSingleRequest(String submissionId) {
        return "submission:%s:isSingleRequest".formatted(submissionId);
    }
}