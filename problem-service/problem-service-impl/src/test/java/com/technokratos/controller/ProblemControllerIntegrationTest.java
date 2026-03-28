package com.technokratos.controller;

import com.technokratos.dto.request.RunRequest;
import com.technokratos.dto.response.ProblemResponse;
import com.technokratos.enums.Difficulty;
import com.technokratos.enums.PublishStatus;
import com.technokratos.BaseIntegrationTest;
import com.technokratos.entity.Problem;
import com.technokratos.repository.ProblemRepository;
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
public class ProblemControllerIntegrationTest extends BaseIntegrationTest {

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
        problem.setReadyForPublish(true);
        problem.setPublishStatus(PublishStatus.NOT_PUBLISHED);
        problem = problemRepository.save(problem);
        testProblemId = problem.getId();
    }

    @Test
    void findById() {
        ResponseEntity<ProblemResponse> response = restTemplate.exchange(
                "/api/v1/problems/%s".formatted(testProblemId),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProblemResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testProblemId, response.getBody().id());
    }

    @Test
    void getAll() {
        ResponseEntity<List<ProblemResponse>> response = restTemplate.exchange(
                "/api/v1/problems/",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void run() {
        RunRequest request = createRunRequest();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/problems/run",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                Void.class
        );

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void submit() {
        RunRequest request = createRunRequest();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/problems/submit",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                Void.class
        );

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    private ProblemResponse createTestProblem() {
        return new ProblemResponse(
                UUID.randomUUID(), "Test Problem", "Description", "Constraints",
                Difficulty.MEDIUM, List.of(), List.of()
        );
    }

    private RunRequest createRunRequest() {
        return new RunRequest(
                UUID.randomUUID(), testProblemId, UUID.randomUUID(), "code", 1
        );
    }
}