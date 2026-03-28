package com.technokratos.kafka.producer;

import com.technokratos.dto.request.PublishProblemsRequest;
import com.technokratos.dto.request.PublishTestcasesRequest;
import com.technokratos.dto.request.RunRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEventToRunTopic(RunRequest request) {
        log.debug("{}", request);
        kafkaTemplate.send("run-topic", request.id().toString(), request);
    }

    public void sendEventToSubmitTopic(RunRequest request) {
        kafkaTemplate.send("submit-topic", request.id().toString(), request);
    }

    public void sendEventToRunWithWrapperTopic(RunRequest request) {
        log.debug("{}", request);
        kafkaTemplate.send("run-with-wrapper-topic", request.id().toString(), request);
    }

    public void sendEventToSubmitWithWrapperTopic(RunRequest request) {
        kafkaTemplate.send("submit-with-wrapper-topic", request.id().toString(), request);
    }

    public void sendEventToPublishTestcasesTopic(PublishTestcasesRequest publishTestcasesRequest) {
        kafkaTemplate.send("publish-testcases-topic", publishTestcasesRequest.problemId().toString(), publishTestcasesRequest);
    }

    public void sendEventToPublishProblemsTopic(PublishProblemsRequest publishProblemsRequest) {
        kafkaTemplate.send("publish-problems-topic", publishProblemsRequest.id().toString(), publishProblemsRequest);
    }
}
