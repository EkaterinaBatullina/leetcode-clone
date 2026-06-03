package com.technokratos.client;

import com.technokratos.config.property.OAuth2ClientProperties;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.response.TokenCoupleResponse;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Objects;

/*
 * Используется в интеграционных тестах как обёртка над HTTP-вызовами,
 * чтобы убрать дублирование TestRestTemplate.exchange и работы с headers.
 *
 * Отвечает за:
 * - регистрацию пользователей
 * - логин
 * - получение токенов
 *
 * Не содержит бизнес-логики.
 */
public class TestAuthClient {
    private final TestRestTemplate restTemplate;
    private final OAuth2ClientProperties properties;

    public TestAuthClient(TestRestTemplate restTemplate,
                          OAuth2ClientProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public String register(String username, String email, String password) {
        ResponseEntity<TokenCoupleResponse> response = restTemplate.exchange(
                "/api/v1/authentication/register",
                HttpMethod.POST,
                new HttpEntity<>(
                        new UserFullRequest(username, email, password),
                        basicAuthHeaders()
                ),
                TokenCoupleResponse.class
        );

        return Objects.requireNonNull(response.getBody()).accessToken();
    }

    public String loginAsAdmin() {
        ResponseEntity<TokenCoupleResponse> response = restTemplate.exchange(
                "/api/v1/authentication/login",
                HttpMethod.POST,
                new HttpEntity<>(
                        new AuthenticationRequest("adminUser", "adminPassword"),
                        basicAuthHeaders()
                ),
                TokenCoupleResponse.class
        );

        return Objects.requireNonNull(response.getBody()).accessToken();
    }

    private HttpHeaders basicAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(
                properties.getClientId(),
                properties.getClientSecret()
        );
        return headers;
    }
}