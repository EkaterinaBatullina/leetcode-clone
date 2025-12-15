package com.technokratos.controller;

import com.technokratos.config.TestRestTemplateConfig;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.RefreshTokenRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.response.TokenCoupleResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestRestTemplateConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerIntegrationTest {
    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    JwtDecoder decoder;
    String accessToken;
    String refreshToken;

    @Test
    void register() {
        HttpEntity<UserFullRequest> request = new HttpEntity<>(
                new UserFullRequest("username6", "email6@gmail.com", "securePassword123"),
                createBasicAuthHeaders()
        );
        ResponseEntity<TokenCoupleResponse> response = testRestTemplate.exchange(
                "/api/v1/authentication/register",
                HttpMethod.POST,
                request,
                TokenCoupleResponse.class
        );

        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(201)));

        TokenCoupleResponse body = response.getBody();
        assertNotNull(body);

        accessToken = body.accessToken();
        refreshToken = body.refreshToken();

        assertNotNull(accessToken);
        assertNotNull(refreshToken);
    }

    @Test
    void login() {
        HttpEntity<AuthenticationRequest> request = new HttpEntity<>(
                new AuthenticationRequest("username6", "securePassword123"),
                createBasicAuthHeaders()
        );
        ResponseEntity<TokenCoupleResponse> response = testRestTemplate.exchange(
                "/api/v1/authentication/login",
                HttpMethod.POST,
                request,
                TokenCoupleResponse.class
        );

        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));

        TokenCoupleResponse body = response.getBody();
        assertNotNull(body);

        accessToken = body.accessToken();
        refreshToken = body.refreshToken();

        assertNotNull(accessToken);
        assertNotNull(refreshToken);
    }

    @Test
    void refresh() {
        HttpEntity<RefreshTokenRequest> request = new HttpEntity<>(
                new RefreshTokenRequest(refreshToken),
                createBearerAuthHeaders()
        );

        ResponseEntity<TokenCoupleResponse> response = testRestTemplate.exchange(
                "/api/v1/authentication/token/refresh",
                HttpMethod.POST,
                request,
                TokenCoupleResponse.class
        );

        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));

        TokenCoupleResponse body = response.getBody();
        assertNotNull(body);

        assertNotNull(body.accessToken());
        assertNotNull(body.refreshToken());
    }

    private HttpHeaders createBasicAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("my-client-id", "my-client-secret");
        return headers;
    }

    private HttpHeaders createBearerAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }
}