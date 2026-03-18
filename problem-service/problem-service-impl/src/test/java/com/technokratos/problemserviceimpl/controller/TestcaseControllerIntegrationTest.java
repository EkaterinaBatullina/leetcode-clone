package com.technokratos.problemserviceimpl.controller;

import com.technokratos.problemserviceapi.dto.request.TestcaseRequest;
import com.technokratos.problemserviceapi.dto.response.TestcaseResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.problemserviceapi.enums.PublishStatus;
import com.technokratos.problemserviceimpl.BaseIntegrationTest;
import com.technokratos.problemserviceimpl.entity.Problem;
import com.technokratos.problemserviceimpl.entity.Testcase;
import com.technokratos.problemserviceimpl.repository.ProblemRepository;
import com.technokratos.problemserviceimpl.repository.TestcaseRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestcaseControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestcaseRepository testcaseRepository;
    @Autowired
    private ProblemRepository problemRepository;


    private HttpHeaders headers;
    private UUID testProblemId;
    private UUID testTestcaseId;

    @BeforeAll
    void setup() {
        headers = new HttpHeaders();
        headers.setBearerAuth(createValidToken());

        // Create and save test problem
        Problem problem = new Problem();
        problem.setTitle("Test Problem");
        problem.setDescription("Description");
        problem.setConstraints("Constraints");
        problem.setDifficulty(Difficulty.MEDIUM);
        problem.setReadyForPublish(true);
        problem.setPublishStatus(PublishStatus.NOT_PUBLISHED);
        problem = problemRepository.save(problem);
        testProblemId = problem.getId();

        Testcase testcase = new Testcase();
        testcase.setInputData("input");
        testcase.setExpectedOutput("output");
        testcase.setCpuTimeLimit(2);
        testcase.setMemoryLimit(256);
        testcase.setVisible(false);
        testcase.setProblem(problem);
        testcase = testcaseRepository.save(testcase);
        testTestcaseId = testcase.getId();
    }

    @Test
    void create() {
        TestcaseRequest request = new TestcaseRequest(
                "input", "output", 1000, true, 1000, 512
        );
        ResponseEntity<UUID> response = restTemplate.exchange(
                "/internal/testcases/",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                UUID.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void deleteById() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/internal/testcases/%s".formatted(testTestcaseId),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void findById() {
        ResponseEntity<TestcaseResponse> response = restTemplate.exchange(
                "/internal/testcases/%s".formatted(testTestcaseId),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                TestcaseResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testTestcaseId, response.getBody().id());
        assertEquals("input", response.getBody().inputData());
        assertEquals("output", response.getBody().expectedOutput());
    }

    @Test
    void getAllByProblemId() {
        ResponseEntity<List<TestcaseResponse>> response = restTemplate.exchange(
                "/internal/testcases/problem/%s".formatted(testProblemId),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        TestcaseResponse firstTestcase = response.getBody().get(0);
        assertEquals(testTestcaseId, firstTestcase.id());
        assertEquals("input", firstTestcase.inputData());
        assertEquals("output", firstTestcase.expectedOutput());
    }

    @Test
    void replace() {
        Testcase testcase = new Testcase();
        testcase.setInputData("original");
        testcase.setExpectedOutput("original");
        testcase.setProblem(problemRepository.findById(testProblemId).orElseThrow());
        testcase = testcaseRepository.save(testcase);
        UUID replaceId = testcase.getId();

        TestcaseRequest request = new TestcaseRequest(
                "new input", "new output", 2000, true, 2000, 1024
        );

        ResponseEntity<Void> response = restTemplate.exchange(
                "/internal/testcases/%s".formatted(replaceId),
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Testcase updated = testcaseRepository.findById(replaceId).orElseThrow();
        assertEquals("new input", updated.getInputData());
        assertEquals("new output", updated.getExpectedOutput());
        assertEquals(2000, updated.getCpuTimeLimit());
        assertEquals(1024, updated.getMemoryLimit());
        assertTrue(updated.getVisible());
    }

    @Test
    void update() {
        Testcase testcase = new Testcase();
        testcase.setInputData("original");
        testcase.setExpectedOutput("original");
        testcase.setVisible(false);
        testcase.setProblem(problemRepository.findById(testProblemId).orElseThrow());
        testcase = testcaseRepository.save(testcase);
        UUID updateId = testcase.getId();

        TestcaseRequest request = new TestcaseRequest(
                "updated input", "original", 0, true, 0, 0
        );

        ResponseEntity<Void> response = restTemplate.exchange(
                "/internal/testcases/%s".formatted(updateId),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Testcase updated = testcaseRepository.findById(updateId).orElseThrow();
        assertEquals("updated input", updated.getInputData());
        assertEquals("original", updated.getExpectedOutput());  // Should remain unchanged
        assertTrue(updated.getVisible());  // Should be updated
    }
}