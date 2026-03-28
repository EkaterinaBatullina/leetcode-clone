package com.technokratos.handler;

import com.technokratos.dto.response.RunResponse;
import com.technokratos.enums.Action;
import com.technokratos.kafka.producer.KafkaProducerService;
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

