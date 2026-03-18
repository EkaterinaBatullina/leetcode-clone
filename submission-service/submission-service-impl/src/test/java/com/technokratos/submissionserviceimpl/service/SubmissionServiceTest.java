package com.technokratos.submissionserviceimpl.service;

import com.technokratos.submissionserviceapi.dto.request.SubmissionRequest;
import com.technokratos.submissionserviceapi.dto.response.SubmissionResponse;
import com.technokratos.submissionserviceapi.enums.SubmissionStatus;
import com.technokratos.submissionserviceimpl.entity.Submission;
import com.technokratos.submissionserviceimpl.repository.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
class SubmissionServiceTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @MockitoBean
    private SubmissionRepository submissionRepository;

    private Submission testSubmission;
    private static final UUID TEST_SUBMISSION_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
        testSubmission = Submission.builder()
                .id(TEST_SUBMISSION_ID.toString())
                .userId(UUID.randomUUID())
                .problemId(UUID.randomUUID())
                .languageId(71)
                .sourceCode("print('Hello')")
                .status(SubmissionStatus.PENDING)
                .createdAt(Instant.now())
                .responses(new ArrayList<>()) // Use mutable list here
                .build();
    }

    @Test
    void createSubmission_Success() {
        SubmissionRequest request = new SubmissionRequest(
                TEST_SUBMISSION_ID,
                UUID.randomUUID(),
                UUID.randomUUID(),
                71,
                "print('Hello')",
                SubmissionStatus.PENDING,
                Instant.now(),
                List.of()
        );

        when(submissionRepository.save(any(Submission.class))).thenReturn(testSubmission);

        String id = submissionService.create(request);

        assertNotNull(id);
        assertEquals(TEST_SUBMISSION_ID.toString(), id);
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    void findById_Exists() {
        when(submissionRepository.findById(TEST_SUBMISSION_ID.toString()))
                .thenReturn(Optional.of(testSubmission));

        SubmissionResponse response = submissionService.findById(TEST_SUBMISSION_ID.toString());

        assertNotNull(response);
        assertEquals(TEST_SUBMISSION_ID.toString(), response.id().toString());
    }

    @Test
    void findById_NotFound() {
        when(submissionRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                submissionService.findById("invalid-id")
        );
    }

    @Test
    void isFirstSuccessfulAttempt_True() {
        UUID userId = UUID.randomUUID();
        UUID problemId = UUID.randomUUID();

        when(submissionRepository.existsByUserIdAndProblemIdAndStatus(
                userId, problemId, SubmissionStatus.SOLVED
        )).thenReturn(false);

        assertTrue(submissionService.isFirstSuccessfulAttempt(userId, problemId));
    }

    @Test
    void updateSubmission_Success() {
        SubmissionRequest request = new SubmissionRequest(
                TEST_SUBMISSION_ID,
                testSubmission.getUserId(),
                testSubmission.getProblemId(),
                71,
                "print('Updated')",
                SubmissionStatus.SOLVED,
                Instant.now(),
                List.of()
        );

        when(submissionRepository.findById(TEST_SUBMISSION_ID.toString())).thenReturn(Optional.of(testSubmission));

        submissionService.update(TEST_SUBMISSION_ID.toString(), request);

        verify(submissionRepository).save(testSubmission);
        assertEquals(71, testSubmission.getLanguageId());
        assertEquals("print('Updated')", testSubmission.getSourceCode());
        assertEquals(SubmissionStatus.SOLVED, testSubmission.getStatus());
    }
}