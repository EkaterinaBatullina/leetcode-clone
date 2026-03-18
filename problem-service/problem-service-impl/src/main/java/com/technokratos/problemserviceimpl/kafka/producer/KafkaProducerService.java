package com.technokratos.problemserviceimpl.kafka.producer;

import com.technokratos.problemserviceapi.dto.request.PublishProblemsRequest;
import com.technokratos.problemserviceapi.dto.request.PublishTestcasesRequest;
import com.technokratos.problemserviceapi.dto.request.RunRequest;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
=======
import lombok.extern.slf4j.Slf4j;
>>>>>>> feature/problem-and-submission-service
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
<<<<<<< HEAD
=======
@Slf4j
>>>>>>> feature/problem-and-submission-service
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEventToRunTopic(RunRequest request) {
<<<<<<< HEAD
=======
        log.debug("{}", request);
>>>>>>> feature/problem-and-submission-service
        kafkaTemplate.send("run-topic", request.id().toString(), request);
    }

    public void sendEventToSubmitTopic(RunRequest request) {
        kafkaTemplate.send("submit-topic", request.id().toString(), request);
    }

<<<<<<< HEAD
=======
    public void sendEventToRunWithWrapperTopic(RunRequest request) {
        log.debug("{}", request);
        kafkaTemplate.send("run-with-wrapper-topic", request.id().toString(), request);
    }

    public void sendEventToSubmitWithWrapperTopic(RunRequest request) {
        kafkaTemplate.send("submit-with-wrapper-topic", request.id().toString(), request);
    }

>>>>>>> feature/problem-and-submission-service
    public void sendEventToPublishTestcasesTopic(PublishTestcasesRequest publishTestcasesRequest) {
        kafkaTemplate.send("publish-testcases-topic", publishTestcasesRequest.problemId().toString(), publishTestcasesRequest);
    }

    public void sendEventToPublishProblemsTopic(PublishProblemsRequest publishProblemsRequest) {
        kafkaTemplate.send("publish-problems-topic", publishProblemsRequest.id().toString(), publishProblemsRequest);
    }
}
