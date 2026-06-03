package com.technokratos.controller;

import com.technokratos.config.TestRestTemplateConfig;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.RefreshTokenRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.response.TokenCoupleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestRestTemplateConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthenticationControllerIntegrationTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JwtDecoder decoder;

    @Test
    void register() {
        ResponseEntity<TokenCoupleResponse> response =
                registerUser("username-register","register@gmail.com","securePassword123");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        TokenCoupleResponse body = response.getBody();

        assertNotNull(body);
        assertNotNull(body.accessToken());
        assertNotNull(body.refreshToken());

        /*
         * Проверяем, что JWT содержит идентификатор
         * зарегистрированного пользователя.
         */
        assertNotNull(decoder.decode(body.accessToken()).getSubject());
    }

    @Test
    void login() {
        registerUser("username-login", "login@gmail.com", "securePassword123");

        ResponseEntity<TokenCoupleResponse> response =
                loginUser("username-login","securePassword123");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        TokenCoupleResponse body = response.getBody();

        assertNotNull(body);
        assertNotNull(body.accessToken());
        assertNotNull(body.refreshToken());
    }

    /*
     * Проверяем сценарий обновления токенов.
     *
     * Пользователь проходит аутентификацию,
     * получает пару access/refresh токенов,
     * после чего использует refresh token
     * для получения новой пары токенов.
     */
    @Test
    void refresh() {
        registerUser("username-refresh","refresh@gmail.com","securePassword123");

        TokenCoupleResponse tokens = Objects
                .requireNonNull(loginUser("username-refresh","securePassword123").getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokens.accessToken());

        ResponseEntity<TokenCoupleResponse> response =
                testRestTemplate.exchange(
                        "/api/v1/authentication/token/refresh",
                        HttpMethod.POST,
                        new HttpEntity<>(
                                new RefreshTokenRequest(
                                        tokens.refreshToken()
                                ),
                                headers
                        ),
                        TokenCoupleResponse.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        TokenCoupleResponse body = response.getBody();

        assertNotNull(body);
        assertNotNull(body.accessToken());
        assertNotNull(body.refreshToken());
    }

    /*
     * Client credentials используются для
     * аутентификации OAuth2 клиента.
     */
    private HttpHeaders createBasicAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("my-client-id", "my-client-secret");
        return headers;
    }

    private ResponseEntity<TokenCoupleResponse> registerUser(String username, String email, String password) {
        return testRestTemplate.exchange("/api/v1/authentication/register",
                HttpMethod.POST, new HttpEntity<>(new UserFullRequest(username, email, password),
                        createBasicAuthHeaders()
                ), TokenCoupleResponse.class
        );
    }

    private ResponseEntity<TokenCoupleResponse> loginUser(String username, String password) {
        return testRestTemplate.exchange("/api/v1/authentication/login",
                HttpMethod.POST, new HttpEntity<>(new AuthenticationRequest(username, password),
                        createBasicAuthHeaders()
                ),
                TokenCoupleResponse.class
        );
    }
}