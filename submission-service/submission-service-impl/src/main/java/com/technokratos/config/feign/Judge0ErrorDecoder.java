package com.technokratos.config.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class Judge0ErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            if (response.body() != null) {
                String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
                log.error("judge0 error body: {}", body);
                JsonNode jsonNode = objectMapper.readTree(body);
                String message = jsonNode.has("message") ? jsonNode.get("message").asText() : "unknown error";
                return new IllegalStateException(
                        "Judge0 API returned error status %d with message: %s".formatted(response.status(), message));
            } else {
                return new IllegalStateException(
                        "Judge0 HTTP request failed with status %d: empty response body".formatted(response.status()));
            }
        } catch (Exception e) {
            return new IllegalStateException(
                    "Critical failure while decoding Judge0 response with status %d: invalid JSON format".formatted(response.status()), e);
        }
    }
}

