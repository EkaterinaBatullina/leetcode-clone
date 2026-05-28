package com.technokratos.kafka.producer;

import com.technokratos.dto.request.ProblemsPublishedResponse;
import com.technokratos.dto.response.PublishTestcasesResponse;
import com.technokratos.dto.response.RunResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEventToRunResponseTopic(RunResponse response) {
        kafkaTemplate.send("run-response-topic", response.id().toString(), response);
    }

    public void sendEventToSubmitResponseTopic(RunResponse response) {
        kafkaTemplate.send("submit-response-topic", response.id().toString(), response);
    }

    public void sendEventToRunWithWrapperResponseTopic(RunResponse response) {
        kafkaTemplate.send("run-with-wrapper-response-topic", response.id().toString(), response);
    }

    public void sendEventToSubmitWithWrapperResponseTopic(RunResponse response) {
        kafkaTemplate.send("submit-with-wrapper-response-topic", response.id().toString(), response);
    }

    public void sendEventToProblemsPublishedResponseTopic(ProblemsPublishedResponse response) {
        kafkaTemplate.send("problems-published-response-topic", response.problemId().toString(), response);
    }

    public void sendToPublishProblemsFailedTopic(ProblemsPublishedResponse response) {
        kafkaTemplate.send("publish-problems-failed-topic", response.problemId().toString(), response);
    }

    public void sendToPublishTestcasesResponseTopic(PublishTestcasesResponse response) {
        kafkaTemplate.send("publish-testcases-response-topic", response.problemId().toString(), response);
    }
}
