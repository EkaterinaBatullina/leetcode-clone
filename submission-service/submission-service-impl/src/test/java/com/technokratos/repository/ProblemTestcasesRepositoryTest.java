package com.technokratos.repository;

import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.entity.*;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
class ProblemTestcasesRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ProblemTestcasesRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private ProblemTestcases createTestProblem() {
        List<Testcase> testcases = List.of(
                new Testcase(UUID.randomUUID(), "input1", "output1", true, 100, 128),
                new Testcase(UUID.randomUUID(), "input2", "output2", false, 200, 256)
        );

        return ProblemTestcases.builder()
                .id(UUID.randomUUID())
                .difficulty(Difficulty.MEDIUM)
                .testcases(testcases)
                .inputs("input1\ninput2")
                .outputs("output1\noutput2")
                .visibleInputs("input1")
                .visibleOutputs("output1")
                .cpuTimeLimit(300)
                .memoryLimit(500)
                .visibleCpuTimeLimit(150)
                .visibleMemoryLimit(250)
                .build();
    }

    @Test
    void findById_Success() {
        ProblemTestcases problem = createTestProblem();
        mongoTemplate.save(problem);

        ProblemTestcases found = repository.findById(problem.getId());

        assertNotNull(found);
        assertEquals(problem.getId(), found.getId());
        assertEquals(2, found.getTestcases().size());
    }

    @Test
    void findProblemDifficultyById_Success() {
        ProblemTestcases problem = createTestProblem();
        mongoTemplate.save(problem);

        Optional<ProblemDifficulty> result = repository.findProblemDifficultyById(problem.getId());

        assertTrue(result.isPresent());
        assertEquals(Difficulty.MEDIUM, result.get().getDifficulty());
    }

    @Test
    void findIOAndLimitsById_Success() {
        ProblemTestcases problem = createTestProblem();
        mongoTemplate.save(problem);

        ProblemIOAndLimits result = repository.findIOAndLimitsById(problem.getId());

        assertNotNull(result);
        assertEquals("input1\ninput2", result.getInputs());
        assertEquals("output1\noutput2", result.getOutputs());
        assertEquals(300, result.getCpuTimeLimit());
        assertEquals(500, result.getMemoryLimit());
    }

    @Test
    void findVisibleIOAndLimitsById_Success() {
        ProblemTestcases problem = createTestProblem();
        mongoTemplate.save(problem);

        ProblemVisibleIOAndLimits result = repository.findVisibleIOAndLimitsById(problem.getId());

        assertNotNull(result);
        assertEquals("input1", result.getVisibleInputs());
        assertEquals("output1", result.getVisibleOutputs());
        assertEquals(150, result.getVisibleCpuTimeLimit());
        assertEquals(250, result.getVisibleMemoryLimit());
    }

    @AfterEach
    void cleanUp() {
        mongoTemplate.dropCollection(ProblemTestcases.class);
    }
}