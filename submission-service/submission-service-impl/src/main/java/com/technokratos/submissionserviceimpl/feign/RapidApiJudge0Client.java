package com.technokratos.submissionserviceimpl.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.technokratos.submissionserviceapi.dto.request.Judge0BatchRequest;
import com.technokratos.submissionserviceimpl.config.feign.FeignConfig;
import com.technokratos.submissionserviceimpl.config.feign.RapidApiFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "rapidapi-judge0-client",
        url = "https://judge0-ce.p.rapidapi.com",
        configuration = FeignConfig.class,
        fallbackFactory = RapidApiFallbackFactory.class
)
public interface RapidApiJudge0Client {
    @PostMapping(value = "/submissions/batch?base64_encoded=false&wait=false", produces = "application/json")
    JsonNode sendBatch(
            @RequestHeader("X-RapidAPI-Key") String apiKey,
            @RequestHeader("X-RapidAPI-Host") String apiHost,
            @RequestBody Judge0BatchRequest request
    );
}