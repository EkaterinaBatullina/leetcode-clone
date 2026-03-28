package com.technokratos.service;

import com.technokratos.dto.request.PublishTestcasesRequest;
import com.technokratos.enums.Difficulty;
import com.technokratos.enums.PublishStatus;
import com.technokratos.BaseServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
public class PublishingCoordinatorServiceTest extends BaseServiceTest {

    @Autowired
    private PublishingCoordinatorService publishingCoordinatorService;

    @Test
    void publishWithAck() {
        UUID problemId = UUID.randomUUID();
        PublishTestcasesRequest request = new PublishTestcasesRequest(
                problemId, Difficulty.MEDIUM, List.of()
        );

        publishingCoordinatorService.publishWithAck(request);
        verify(kafkaProducerService).sendEventToPublishTestcasesTopic(request);
    }

    @Test
    void completeAck() throws Exception {
        UUID problemId = UUID.randomUUID();
        PublishTestcasesRequest request = new PublishTestcasesRequest(
                problemId, Difficulty.EASY, List.of()
        );

        CompletableFuture<PublishStatus> future = publishingCoordinatorService.publishWithAck(request);
        publishingCoordinatorService.completeAck(problemId, PublishStatus.PUBLISHED);
        assertEquals(PublishStatus.PUBLISHED, future.get(1, TimeUnit.SECONDS));
    }
}