package com.technokratos.repository;

import com.technokratos.BaseRepositoryTest;
import com.technokratos.entity.Problem;
import com.technokratos.entity.Testcase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TestcaseRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TestcaseRepository testcaseRepository;

    private Problem problem1;
    private Problem problem2;
    private Testcase testcase1;
    private Testcase testcase2;
    private Testcase testcase3;

    @BeforeEach
    void setUp() {
        // Create problems
        problem1 = Problem.builder()
                .title("Problem 1")
                .description("Description 1")
                .constraints("Constraints 1")
                .build();

        problem2 = Problem.builder()
                .title("Problem 2")
                .description("Description 2")
                .constraints("Constraints 2")
                .build();

        entityManager.persist(problem1);
        entityManager.persist(problem2);
        entityManager.flush();

        // Create testcases
        testcase1 = Testcase.builder()
                .inputData("input1")
                .expectedOutput("output1")
                .problem(problem1)
                .visible(true)
                .build();

        testcase2 = Testcase.builder()
                .inputData("input2")
                .expectedOutput("output2")
                .problem(problem1)
                .cpuTimeLimit(5)
                .memoryLimit(1024)
                .visible(false)
                .build();

        testcase3 = Testcase.builder()
                .inputData("input3")
                .expectedOutput("output3")
                .problem(problem2)
                .build();

        entityManager.persist(testcase1);
        entityManager.persist(testcase2);
        entityManager.persist(testcase3);
        entityManager.flush();
    }

    @Test
    void findAllByProblemId_shouldReturnTestcasesForSpecificProblem() {
        // When
        List<Testcase> result = testcaseRepository.findAllByProblemId(problem1.getId());

        // Then
        assertEquals(2, result.size(), "Should find 2 testcases for problem1");
        assertTrue(result.contains(testcase1), "Should contain testcase1");
        assertTrue(result.contains(testcase2), "Should contain testcase2");
    }

    @Test
    void findAllByProblemId_shouldReturnEmptyListForProblemWithoutTestcases() {
        // Given - Create a new problem with no testcases
        Problem newProblem = Problem.builder()
                .title("New Problem")
                .description("Description")
                .constraints("Constraints")
                .build();
        entityManager.persist(newProblem);
        entityManager.flush();

        // When
        List<Testcase> result = testcaseRepository.findAllByProblemId(newProblem.getId());

        // Then
        assertTrue(result.isEmpty(), "Should return empty list for problem with no testcases");
    }

    @Test
    void findAllByProblemId_shouldNotReturnTestcasesForOtherProblems() {
        // When
        List<Testcase> result = testcaseRepository.findAllByProblemId(problem1.getId());

        // Then
        assertEquals(2, result.size(), "Should only return testcases for problem1");
        assertFalse(result.contains(testcase3), "Should not contain testcase3 from problem2");
    }

    @Test
    void findAllByProblemId_shouldReturnAllTestcaseProperties() {
        // When
        List<Testcase> result = testcaseRepository.findAllByProblemId(problem1.getId());
        Testcase foundTestcase = result.stream()
                .filter(tc -> tc.getId().equals(testcase2.getId()))
                .findFirst()
                .orElseThrow();

        // Then
        assertEquals(testcase2.getInputData(), foundTestcase.getInputData());
        assertEquals(testcase2.getExpectedOutput(), foundTestcase.getExpectedOutput());
        assertEquals(testcase2.getCpuTimeLimit(), foundTestcase.getCpuTimeLimit());
        assertEquals(testcase2.getMemoryLimit(), foundTestcase.getMemoryLimit());
        assertEquals(testcase2.getVisible(), foundTestcase.getVisible());
        assertEquals(problem1.getId(), foundTestcase.getProblem().getId());
    }

    @Test
    void findAllByProblemId_shouldReturnEmptyListForNullProblemId() {
        // When
        List<Testcase> result = testcaseRepository.findAllByProblemId(null);

        // Then
        assertTrue(result.isEmpty(), "Should return empty list for null problem ID");
    }

    @Test
    void findAllByProblemId_shouldReturnEmptyListForNonExistentProblemId() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        List<Testcase> result = testcaseRepository.findAllByProblemId(nonExistentId);

        // Then
        assertTrue(result.isEmpty(), "Should return empty list for non-existent problem ID");
    }

    @Test
    void findAllByProblemId_shouldMaintainRelationshipIntegrity() {
        // When
        List<Testcase> result = testcaseRepository.findAllByProblemId(problem1.getId());

        // Then
        result.forEach(testcase ->
                assertEquals(problem1.getId(), testcase.getProblem().getId(),
                        "All testcases should belong to problem1")
        );
    }
}