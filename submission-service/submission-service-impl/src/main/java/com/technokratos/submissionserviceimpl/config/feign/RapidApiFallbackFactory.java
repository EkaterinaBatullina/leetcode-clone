package com.technokratos.submissionserviceimpl.config.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.technokratos.submissionserviceapi.dto.request.Judge0BatchRequest;
import com.technokratos.submissionserviceimpl.feign.RapidApiJudge0Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RapidApiFallbackFactory implements FallbackFactory<RapidApiJudge0Client> {
    @Override
    public RapidApiJudge0Client create(Throwable cause) {
        return new RapidApiJudge0Client() {
            @Override
            public JsonNode sendBatch(String apiKey, String apiHost, Judge0BatchRequest request) {
                log.error("rapid api fallback triggered: {}", cause.toString());
                throw new RuntimeException("rapid api call failed", cause);
            }
        };

    }
}