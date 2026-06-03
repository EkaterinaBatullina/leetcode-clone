package com.technokratos.client;

import com.technokratos.dto.CustomPageImpl;
import com.technokratos.dto.request.RoleRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.request.UserPartialRequest;
import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.dto.response.UserResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;

/*
 * Инкапсулирует HTTP-вызовы к UserController,
 * чтобы тесты работали на уровне сценариев, а не HTTP деталей.
 *
 * Отвечает за:
 * - получение данных пользователя
 * - обновление профиля
 * - удаление пользователя
 * - операции админа
 *
 * Убирает дублирование:
 * TestRestTemplate.exchange + headers + URL construction.
 *
 * Не содержит бизнес-логики и не выполняет проверок.
 */
public class TestUserClient {
    private final TestRestTemplate restTemplate;

    public TestUserClient(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<UserResponse> getMe(String token) {
        return restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                new HttpEntity<>(bearer(token)),
                UserResponse.class
        );
    }

    public ResponseEntity<StatisticResponse> getStatistic(String token) {
        return restTemplate.exchange(
                "/api/v1/users/me/statistic",
                HttpMethod.GET,
                new HttpEntity<>(bearer(token)),
                StatisticResponse.class
        );
    }

    public ResponseEntity<UserResponse> getByUsername(String username, String token) {
        return restTemplate.exchange(
                "/api/v1/users/%s".formatted(username),
                HttpMethod.GET,
                new HttpEntity<>(bearer(token)),
                UserResponse.class
        );
    }

    public ResponseEntity<CustomPageImpl<UserResponse>> getAll(String token) {
        return restTemplate.exchange(
                "/api/v1/users",
                HttpMethod.GET,
                new HttpEntity<>(bearer(token)),
                new ParameterizedTypeReference<>() {}
        );
    }

    public ResponseEntity<Void> updateMe(String token, UserFullRequest request) {
        return restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.PUT,
                new HttpEntity<>(request, bearer(token)),
                Void.class
        );
    }

    public ResponseEntity<Void> patchMe(String token, UserPartialRequest request) {
        return restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.PATCH,
                new HttpEntity<>(request, bearer(token)),
                Void.class
        );
    }

    public ResponseEntity<Void> deleteMe(String token) {
        return restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.DELETE,
                new HttpEntity<>(bearer(token)),
                Void.class
        );
    }

    public ResponseEntity<Void> updateRole(String token, String userId, RoleRequest request) {
        return restTemplate.exchange(
                "/api/v1/users/%s/role".formatted(userId),
                HttpMethod.PATCH,
                new HttpEntity<>(request, bearer(token)),
                Void.class
        );
    }

    private HttpHeaders bearer(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}