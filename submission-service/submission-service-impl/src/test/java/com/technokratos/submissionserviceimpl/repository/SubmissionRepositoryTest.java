package com.technokratos.submissionserviceimpl.repository;

import com.technokratos.submissionserviceapi.enums.SubmissionStatus;
import com.technokratos.submissionserviceimpl.entity.Submission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
class SubmissionRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private SubmissionRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Submission createTestSubmission(UUID userId, UUID problemId, SubmissionStatus status) {
        return Submission.builder()
            .userId(userId)
            .problemId(problemId)
            .languageId(71)
            .sourceCode("print('Hello')")
            .status(status)
            .createdAt(Instant.now())
            .responses(List.of())
            .build();
    }

    @Test
    void findByUserIdAndProblemId_Success() {
        UUID userId = UUID.randomUUID();
        UUID problemId = UUID.randomUUID();
        
        Submission sub1 = createTestSubmission(userId, problemId, SubmissionStatus.SOLVED);
        Submission sub2 = createTestSubmission(userId, problemId, SubmissionStatus.ATTEMPTED);
        mongoTemplate.save(sub1);
        mongoTemplate.save(sub2);
        
        List<Submission> results = repository.findByUserIdAndProblemId(userId, problemId);
        
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(s -> s.getStatus() == SubmissionStatus.SOLVED));
        assertTrue(results.stream().anyMatch(s -> s.getStatus() == SubmissionStatus.ATTEMPTED));
    }

    @Test
    void findAllByUserId_Success() {
        UUID userId = UUID.randomUUID();
        
        Submission sub1 = createTestSubmission(userId, UUID.randomUUID(), SubmissionStatus.SOLVED);
        Submission sub2 = createTestSubmission(userId, UUID.randomUUID(), SubmissionStatus.ATTEMPTED);
        mongoTemplate.save(sub1);
        mongoTemplate.save(sub2);
        
        List<Submission> results = repository.findAllByUserId(userId);
        
        assertEquals(2, results.size());
        assertEquals(userId, results.get(0).getUserId());
        assertEquals(userId, results.get(1).getUserId());
    }

    @Test
    void existsByUserIdAndProblemIdAndStatus_Exists() {
        UUID userId = UUID.randomUUID();
        UUID problemId = UUID.randomUUID();
        
        mongoTemplate.save(createTestSubmission(userId, problemId, SubmissionStatus.SOLVED));
        
        boolean exists = repository.existsByUserIdAndProblemIdAndStatus(
            userId, problemId, SubmissionStatus.SOLVED
        );
        
        assertTrue(exists);
    }

    @Test
    void existsByUserIdAndProblemIdAndStatus_NotExists() {
        UUID userId = UUID.randomUUID();
        UUID problemId = UUID.randomUUID();
        
        mongoTemplate.save(createTestSubmission(userId, problemId, SubmissionStatus.ATTEMPTED));
        
        boolean exists = repository.existsByUserIdAndProblemIdAndStatus(
            userId, problemId, SubmissionStatus.SOLVED
        );
        
        assertFalse(exists);
    }

    @AfterEach
    void cleanUp() {
        mongoTemplate.dropCollection(Submission.class);
    }
}