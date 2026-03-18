package com.technokratos.submissionserviceimpl.config.feign;

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
                return new RuntimeException("judge0 error (" + response.status() + "): " + message);
            } else {
                return new RuntimeException("judge0 error (" + response.status() + "): empty response body");
            }
        } catch (Exception e) {
            return new RuntimeException("judge0 error (" + response.status() + "): failed to parse error", e);
        }
    }
}

