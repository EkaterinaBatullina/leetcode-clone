package com.technokratos.kafka.consumer;

import com.technokratos.problemserviceapi.dto.request.ProblemsPublishedResponse;
import com.technokratos.problemserviceapi.dto.request.PublishProblemsRequest;
import com.technokratos.problemserviceapi.dto.request.PublishTestcasesRequest;
import com.technokratos.problemserviceapi.dto.request.RunRequest;
import com.technokratos.problemserviceapi.dto.response.PublishTestcasesResponse;
import com.technokratos.problemserviceapi.enums.PublishStatus;
import com.technokratos.enums.Action;
import com.technokratos.kafka.producer.KafkaProducerService;
import com.technokratos.service.ProblemTestcasesService;
import com.technokratos.service.base.BaseJudge0Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class KafkaConsumerService {
    private final BaseJudge0Service judge0Service;
    private final ProblemTestcasesService problemTestcasesService;
    private final KafkaProducerService kafkaProducerService;

    public KafkaConsumerService(@Qualifier("judge0Service") BaseJudge0Service judge0Service, ProblemTestcasesService problemTestcasesService, KafkaProducerService kafkaProducerService) {
        this.judge0Service = judge0Service;
        this.problemTestcasesService = problemTestcasesService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @KafkaListener(topics = "run-topic", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumeRunRequest(RunRequest request) {
        log.debug("received run request for submission: {}", request.id());
        judge0Service.sendBatchSubmission(request, Action.RUN);
    }

    @KafkaListener(topics = "submit-topic", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumeSubmitRequest(RunRequest request) {
        log.debug("received submit request: {}", request);
        judge0Service.sendBatchSubmission(request, Action.SUBMIT);
    }

    @KafkaListener(topics = "run-with-wrapper-topic", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumeRunWithWrapperRequest(RunRequest request) {
        log.debug("received run request with wrapper for submission: {}", request.id());
        log.debug("{}", request);
        judge0Service.sendSubmission(request, Action.RUN);
    }

    @KafkaListener(topics = "submit-with-wrapper-topic", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumeSubmitWithWrapperRequest(RunRequest request) {
        log.debug("received submit with wrapper request: {}", request);
        judge0Service.sendSubmission(request, Action.SUBMIT);
    }

    @KafkaListener(topics = "publish-testcases-topic", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumePublishTestcasesRequest(PublishTestcasesRequest publishTestcasesRequest) {
        try {
            log.debug("received publish testcases request: {}", publishTestcasesRequest.problemId());
            problemTestcasesService.create(publishTestcasesRequest);
            kafkaProducerService.sendToPublishTestcasesResponseTopic(
                    new PublishTestcasesResponse(publishTestcasesRequest.problemId(), PublishStatus.PUBLISHED)
            );
        } catch (Exception e) {
            kafkaProducerService.sendToPublishTestcasesResponseTopic(
                    new PublishTestcasesResponse(publishTestcasesRequest.problemId(), PublishStatus.FAILED)
            );
        }
    }

    @KafkaListener(topics = "publish-problems-topic", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumePublishProblemsRequest(PublishProblemsRequest publishProblemsRequests) {
        log.debug("received publish problems request: {}", publishProblemsRequests.id());
        List<UUID> problemIds = problemTestcasesService.saveAll(publishProblemsRequests.publishTestcasesRequestList());
        for (UUID problemId : problemIds) {
            ProblemsPublishedResponse response = new ProblemsPublishedResponse(problemId);
            kafkaProducerService.sendEventToProblemsPublishedResponseTopic(response);
        }
    }

    @KafkaListener(topics = "publish-problems-topic-dlt", groupId = "submission-service", containerFactory = "kafkaListenerContainerFactory")
    public void consumePublishProblemsFailure(PublishProblemsRequest publishProblemsRequests) {
        log.error("failed message: {}", publishProblemsRequests);
        List<UUID> problemIds = publishProblemsRequests.publishTestcasesRequestList().stream().map(PublishTestcasesRequest::problemId).toList();
        for (UUID problemId : problemIds) {
            ProblemsPublishedResponse response = new ProblemsPublishedResponse(problemId);
            kafkaProducerService.sendToPublishProblemsFailedTopic(response);
        }
    }
}
