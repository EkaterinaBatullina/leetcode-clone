package com.technokratos.controller;

import com.technokratos.dto.request.ProblemRequest;
import com.technokratos.dto.response.ProblemResponse;
import com.technokratos.enums.Difficulty;
import com.technokratos.BaseIntegrationTest;
import com.technokratos.entity.Problem;
import com.technokratos.repository.ProblemRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InternalProblemControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProblemRepository problemRepository;

    private HttpHeaders headers;
    private UUID testProblemId;

    @BeforeAll
    void setup() {
        headers = new HttpHeaders();
        headers.setBearerAuth(createValidToken());
        testProblemId = createTestProblem().id();
        Problem problem = new Problem();
        problem.setTitle("Test Problem");
        problem.setDescription("Description");
        problem.setConstraints("Constraints");
        problem.setDifficulty(Difficulty.MEDIUM);
        problem = problemRepository.save(problem);
        testProblemId = problem.getId();
    }

    @Test
    void create() {
        ProblemRequest request = createProblemRequest();
        ResponseEntity<String> response = restTemplate.exchange(
                "/internal/problems/",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                String.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void replace() {
        ProblemRequest request = createProblemRequest();
        ResponseEntity<Void> response = restTemplate.exchange(
                "/internal/problems/%s".formatted(testProblemId),
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void update() {
        ProblemRequest request = createProblemRequest();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/internal/problems/%s".formatted(testProblemId),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteById() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/internal/problems/%s".formatted(testProblemId),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void publishProblems() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/internal/problems/publish",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                Void.class
        );

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void publishTestcases() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/internal/problems/publish/%s".formatted(testProblemId),
                HttpMethod.POST,
                new HttpEntity<>(headers),
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private ProblemResponse createTestProblem() {
        return new ProblemResponse(
                UUID.randomUUID(), "title", "description", "constraints",
                Difficulty.MEDIUM, new ArrayList<>(), new ArrayList<>()
        );
    }

    private ProblemRequest createProblemRequest(){
        return new ProblemRequest(
                "New Problem", "Description", "Constraints",
                Difficulty.HARD, new ArrayList<>(), new ArrayList<>()
        );
    }
}