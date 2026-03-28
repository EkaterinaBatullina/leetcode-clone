package com.technokratos.kafka.producer;

import com.technokratos.dto.request.PublishProblemsRequest;
import com.technokratos.dto.request.PublishTestcasesRequest;
import com.technokratos.dto.request.RunRequest;
import com.technokratos.enums.Difficulty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    void sendEventToRunTopic() {
        // Given
        RunRequest request = createRunRequest();

        // When
        kafkaProducerService.sendEventToRunTopic(request);

        // Then
        verifySend("run-topic", request.id().toString(), request);
    }

    @Test
    void sendEventToSubmitTopic() {
        // Given
        RunRequest request = createRunRequest();

        // When
        kafkaProducerService.sendEventToSubmitTopic(request);

        // Then
        verifySend("submit-topic", request.id().toString(), request);
    }

    @Test
    void sendEventToRunWithWrapperTopic() {
        // Given
        RunRequest request = createRunRequest();

        // When
        kafkaProducerService.sendEventToRunWithWrapperTopic(request);

        // Then
        verifySend("run-with-wrapper-topic", request.id().toString(), request);
    }

    @Test
    void sendEventToSubmitWithWrapperTopic() {
        // Given
        RunRequest request = createRunRequest();

        // When
        kafkaProducerService.sendEventToSubmitWithWrapperTopic(request);

        // Then
        verifySend("submit-with-wrapper-topic", request.id().toString(), request);
    }

    @Test
    void sendEventToPublishTestcasesTopic() {
        // Given
        UUID problemId = UUID.randomUUID();
        PublishTestcasesRequest request = new PublishTestcasesRequest(
                problemId, Difficulty.MEDIUM, List.of()
        );

        // When
        kafkaProducerService.sendEventToPublishTestcasesTopic(request);

        // Then
        verifySend("publish-testcases-topic", problemId.toString(), request);
    }

    @Test
    void sendEventToPublishProblemsTopic() {
        // Given
        UUID requestId = UUID.randomUUID();
        PublishProblemsRequest request = new PublishProblemsRequest(
                requestId, List.of()
        );

        // When
        kafkaProducerService.sendEventToPublishProblemsTopic(request);

        // Then
        verifySend("publish-problems-topic", requestId.toString(), request);
    }

    private void verifySend(String expectedTopic, String expectedKey, Object expectedValue) {
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

        verify(kafkaTemplate).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                valueCaptor.capture()
        );

        assertEquals(expectedTopic, topicCaptor.getValue());
        assertEquals(expectedKey, keyCaptor.getValue());
        assertEquals(expectedValue, valueCaptor.getValue());

        verifyNoMoreInteractions(kafkaTemplate);
    }

    private RunRequest createRunRequest() {
        return new RunRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "source-code",
                1
        );
    }
}