package com.technokratos.submissionserviceimpl.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.technokratos.submissionserviceapi.dto.request.Judge0BatchRequest;
import com.technokratos.submissionserviceimpl.config.feign.FeignConfig;
import com.technokratos.submissionserviceimpl.config.feign.Judge0FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "judge0-client",
        url = "${judge0.url}",
        configuration = FeignConfig.class,
        fallbackFactory = Judge0FallbackFactory.class
)
public interface Judge0Client {
    @PostMapping(value = "/submissions/batch?base64_encoded=false&wait=false", produces = "application/json")
    JsonNode sendBatch(@RequestBody Judge0BatchRequest request);
}

