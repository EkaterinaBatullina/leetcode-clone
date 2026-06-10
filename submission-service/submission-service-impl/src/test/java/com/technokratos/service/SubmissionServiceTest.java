package com.technokratos.service;

import com.technokratos.dto.request.SubmissionRequest;
import com.technokratos.dto.response.SubmissionResponse;
import com.technokratos.entity.Submission;
import com.technokratos.enums.SubmissionStatus;
import com.technokratos.mapper.SubmissionMapper;
import com.technokratos.repository.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private SubmissionMapper mapper;

    @InjectMocks
    private SubmissionService submissionService;

    private static final UUID TEST_SUBMISSION_ID = UUID.randomUUID();

    private Submission testSubmission;

    @BeforeEach
    void setUp() {
        testSubmission = Submission.builder()
                .id(TEST_SUBMISSION_ID.toString())
                .userId(UUID.randomUUID())
                .problemId(UUID.randomUUID())
                .languageId(71)
                .sourceCode("print('Hello')")
                .status(SubmissionStatus.PENDING)
                .createdAt(Instant.now())
                .responses(new ArrayList<>())
                .build();
    }

    @Test
    void createSubmission_Success() {
        SubmissionRequest request = new SubmissionRequest(
                TEST_SUBMISSION_ID,
                testSubmission.getUserId(),
                testSubmission.getProblemId(),
                71,
                "print('Hello')",
                SubmissionStatus.PENDING,
                Instant.now(),
                List.of()
        );

        when(mapper.toEntity(request)).thenReturn(testSubmission);
        when(submissionRepository.save(any())).thenReturn(testSubmission);

        String result = submissionService.create(request);

        assertEquals(TEST_SUBMISSION_ID.toString(), result);
        verify(submissionRepository).save(any());
    }

    @Test
    void findById_Exists() {
        when(submissionRepository.findById(TEST_SUBMISSION_ID.toString()))
                .thenReturn(Optional.of(testSubmission));

        when(mapper.toResponse(testSubmission))
                .thenReturn(mock(SubmissionResponse.class));

        SubmissionResponse response =
                submissionService.findById(TEST_SUBMISSION_ID.toString());

        assertNotNull(response);
    }

    @Test
    void findById_NotFound() {
        when(submissionRepository.findById("invalid-id"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> submissionService.findById("invalid-id"));
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
}