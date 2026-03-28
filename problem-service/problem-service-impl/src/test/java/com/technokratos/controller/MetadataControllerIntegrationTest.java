package com.technokratos.controller;

import com.technokratos.dto.response.LanguageResponse;
import com.technokratos.dto.response.TagResponse;
import com.technokratos.enums.Difficulty;
import com.technokratos.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetadataControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private HttpHeaders headers;

    @BeforeAll
    void setup() {
        headers = new HttpHeaders();
        headers.setBearerAuth("valid-access-token");
    }

    @Test
    void getDifficulties() {
        ResponseEntity<List<Difficulty>> response = restTemplate.exchange(
                "/internal/metadata/difficulties",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertTrue(response.getBody().containsAll(List.of(
                Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD
        )));
    }

    @Test
    void getTags() {
        ResponseEntity<List<TagResponse>> response = restTemplate.exchange(
                "/internal/metadata/tags",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getLanguages() {
        ResponseEntity<List<LanguageResponse>> response = restTemplate.exchange(
                "/internal/metadata/languages",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}