package com.technokratos.problemserviceimpl.kafka.consumer;

import com.technokratos.problemserviceapi.dto.request.ProblemsPublishedResponse;
import com.technokratos.problemserviceapi.dto.response.PublishTestcasesResponse;
import com.technokratos.problemserviceapi.enums.PublishStatus;
import com.technokratos.problemserviceimpl.service.ProblemService;
import com.technokratos.problemserviceimpl.service.PublishingCoordinatorService;
import com.technokratos.submissionserviceapi.dto.response.RunResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class
KafkaConsumerService {
    private final ProblemService problemService;
    private final PublishingCoordinatorService publishingCoordinatorService;

    @KafkaListener(topics = "submit-with-wrapper-response-topic", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumeSubmitWithWrapperResponse(RunResponse response) {
        log.debug("received submit response for submission: {}", response.id());

    }

    @KafkaListener(topics = "run-with-wrapper-response-topic", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumeRunWithWrapperResponse(RunResponse response) {
        log.debug("received run response for submission: {}", response.id());

    }

    @KafkaListener(topics = "problems-published-response-topic", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumeProblemsPublishedResponse(ProblemsPublishedResponse response) {
        log.debug("marking problem with id {} as published!", response.problemId());
        problemService.markAsPublished(response.problemId());
    }

    @KafkaListener(topics = "publish-problems-failed-topic", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumePublishProblemsFailed(ProblemsPublishedResponse response) {
        log.debug("marking problem with id {} as failed :(((", response.problemId());
        problemService.markAsFailed(response.problemId());
    }

    @KafkaListener(topics = "publish-testcases-response-topic", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumePublishTestcasesResponse(PublishTestcasesResponse response) {
        log.debug("received response {} for publish testcases request", response.problemId());
        publishingCoordinatorService.completeAck(response.problemId(), response.status());
        if (response.status().equals(PublishStatus.PUBLISHED)) problemService.markAsPublished(response.problemId());
        else problemService.markAsFailed(response.problemId());
    }
}