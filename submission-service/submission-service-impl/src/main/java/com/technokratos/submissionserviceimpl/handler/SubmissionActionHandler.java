package com.technokratos.submissionserviceimpl.handler;

import com.technokratos.submissionserviceapi.dto.response.RunResponse;
import com.technokratos.submissionserviceapi.enums.Action;
import com.technokratos.submissionserviceimpl.kafka.producer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubmissionActionHandler {
    private final KafkaProducerService kafkaProducerService;

    public void handle(Action action, RunResponse runResponse) {
        switch (action) {
            case RUN -> kafkaProducerService.sendEventToRunResponseTopic(runResponse);
            case SUBMIT -> kafkaProducerService.sendEventToSubmitResponseTopic(runResponse);
            default -> throw new IllegalArgumentException("unknown action: " + action);
        }
    }
}

