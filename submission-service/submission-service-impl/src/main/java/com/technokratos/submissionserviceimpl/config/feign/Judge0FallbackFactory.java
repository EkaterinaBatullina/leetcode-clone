package com.technokratos.submissionserviceimpl.config.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.technokratos.submissionserviceapi.dto.request.Judge0BatchRequest;
import com.technokratos.submissionserviceimpl.feign.Judge0Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Judge0FallbackFactory implements FallbackFactory<Judge0Client> {
    @Override
    public Judge0Client create(Throwable cause) {
        return new Judge0Client() {
            @Override
            public JsonNode sendBatch(Judge0BatchRequest request) {
                log.error("judge0 fallback triggered: {}", cause.toString());
                throw new RuntimeException("judge0 api call failed", cause);
            }
        };
    }
}
