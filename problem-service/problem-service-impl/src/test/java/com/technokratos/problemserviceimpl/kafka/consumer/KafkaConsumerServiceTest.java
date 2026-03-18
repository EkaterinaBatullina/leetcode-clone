package com.technokratos.problemserviceimpl.kafka.consumer;

import com.technokratos.problemserviceapi.dto.request.ProblemsPublishedResponse;
import com.technokratos.problemserviceapi.dto.response.PublishTestcasesResponse;
import com.technokratos.problemserviceapi.enums.PublishStatus;
import com.technokratos.problemserviceimpl.service.ProblemService;
import com.technokratos.problemserviceimpl.service.PublishingCoordinatorService;
import com.technokratos.submissionserviceapi.dto.response.RunResponse;
import com.technokratos.submissionserviceapi.enums.SubmissionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private ProblemService problemService;

    @Mock
    private PublishingCoordinatorService publishingCoordinatorService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    // Test data
    private final UUID submissionId = UUID.randomUUID();
    private final UUID problemId = UUID.randomUUID();
    private final RunResponse runResponse = new RunResponse(submissionId, SubmissionStatus.SOLVED, List.of());
    private final ProblemsPublishedResponse publishedResponse = new ProblemsPublishedResponse(problemId);
    private final PublishTestcasesResponse testcasesResponse = new PublishTestcasesResponse(problemId, PublishStatus.PUBLISHED);

    @Test
    void consumeRunResponse() {
        // When
        kafkaConsumerService.consumeRunResponse(runResponse);

        // Then
        // Verify no service interactions (only logging in production)
        verifyNoMoreInteractions(problemService, publishingCoordinatorService);
    }

    @Test
    void consumeSubmitResponse() {
        // When
        kafkaConsumerService.consumeSubmitResponse(runResponse);

        // Then
        // Verify no service interactions (only logging in production)
        verifyNoMoreInteractions(problemService, publishingCoordinatorService);
    }

    @Test
    void consumeSubmitWithWrapperResponse() {
        // When
        kafkaConsumerService.consumeSubmitWithWrapperResponse(runResponse);

        // Then
        // Verify no service interactions (only logging in production)
        verifyNoMoreInteractions(problemService, publishingCoordinatorService);
    }

    @Test
    void consumeRunWithWrapperResponse() {
        // When
        kafkaConsumerService.consumeRunWithWrapperResponse(runResponse);

        // Then
        // Verify no service interactions (only logging in production)
        verifyNoMoreInteractions(problemService, publishingCoordinatorService);
    }

    @Test
    void consumeProblemsPublishedResponse() {
        // When
        kafkaConsumerService.consumeProblemsPublishedResponse(publishedResponse);

        // Then
        verify(problemService).markAsPublished(problemId);
        verifyNoMoreInteractions(problemService, publishingCoordinatorService);
    }

    @Test
    void consumePublishProblemsFailed() {
        // When
        kafkaConsumerService.consumePublishProblemsFailed(publishedResponse);

        // Then
        verify(problemService).markAsFailed(problemId);
        verifyNoMoreInteractions(problemService, publishingCoordinatorService);
    }

    @Test
    void consumePublishTestcasesResponse_Published() {
        // Given
        PublishTestcasesResponse response = new PublishTestcasesResponse(problemId, PublishStatus.PUBLISHED);

        // When
        kafkaConsumerService.consumePublishTestcasesResponse(response);

        // Then
        verify(publishingCoordinatorService).completeAck(problemId, PublishStatus.PUBLISHED);
        verify(problemService).markAsPublished(problemId);
        verifyNoMoreInteractions(problemService, publishingCoordinatorService);
    }

    @Test
    void consumePublishTestcasesResponse_Failed() {
        // Given
        PublishTestcasesResponse response = new PublishTestcasesResponse(problemId, PublishStatus.FAILED);

        // When
        kafkaConsumerService.consumePublishTestcasesResponse(response);

        // Then
        verify(publishingCoordinatorService).completeAck(problemId, PublishStatus.FAILED);
        verify(problemService).markAsFailed(problemId);
        verifyNoMoreInteractions(problemService, publishingCoordinatorService);
    }

    // Optional: Test message structure if needed
    private Message<RunResponse> createMessage(RunResponse payload) {
        return MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.RECEIVED_TOPIC, "test-topic")
                .setHeader(KafkaHeaders.RECEIVED_PARTITION, 0)
                .setHeader(KafkaHeaders.OFFSET, 0L)
                .build();
    }
}