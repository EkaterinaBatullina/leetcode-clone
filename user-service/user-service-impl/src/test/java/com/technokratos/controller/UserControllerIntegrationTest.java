package com.technokratos.controller;

import com.technokratos.client.TestAuthClient;
import com.technokratos.client.TestUserClient;
import com.technokratos.config.TestRestTemplateConfig;
import com.technokratos.config.property.OAuth2ClientProperties;
import com.technokratos.dto.CustomPageImpl;
import com.technokratos.dto.request.RoleRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.request.UserPartialRequest;
import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.dto.response.UserResponse;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Поднимаем приложение на случайном порту,
 * чтобы избежать конфликтов между тестовыми
 * запусками и параллельными сборками.
 */
@SpringBootTest(classes = TestRestTemplateConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(com.technokratos.config.TestClientConfig.class)
public class UserControllerIntegrationTest {
    /*
     * Тестирование выполняется через HTTP-клиент,
     * а не прямой вызов контроллера.
     *
     * Это позволяет проверить весь web-слой:
     * security filters, serialization,
     * validation и exception handlers.
     */
    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    JwtDecoder decoder;
    @Autowired
    OAuth2ClientProperties properties;
    @Autowired
    TestAuthClient authClient;
    @Autowired
    TestUserClient userClient;
    String userToken;
    String adminToken;

    @BeforeAll
    void init() {
        userToken = authClient.register("username7", "email7@gmail.com", "securePassword123");
        authClient.register("username10", "email10@gmail.com", "securePassword123");
        adminToken = authClient.loginAsAdmin();
    }

    @Test
    void getMe() {
        ResponseEntity<UserResponse> response = userClient.getMe(userToken);

        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        assertNotNull(response.getBody());
        assertEquals(decoder.decode(userToken).getSubject(), response.getBody().uuid().toString());
    }

    @Test
    void getStatistic() {
        ResponseEntity<StatisticResponse> response = userClient.getStatistic(userToken);

        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        assertNotNull(response.getBody());
        assertEquals(decoder.decode(userToken).getSubject(), response.getBody().userId().toString());
    }

    @Test
    void getByUsername() {
        ResponseEntity<UserResponse> response =
                userClient.getByUsername("username8", userToken);

        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        assertNotNull(response.getBody());
        assertEquals("username8", response.getBody().username());
    }

    @Test
    void getAllForUser() {
        ResponseEntity<CustomPageImpl<UserResponse>> userResponse =
                userClient.getAll(userToken);

        assertTrue(userResponse.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(403)));
    }

    @Test
    void getAllForAdmin() {
        ResponseEntity<CustomPageImpl<UserResponse>> adminResponse =
                userClient.getAll(adminToken);

        assertTrue(adminResponse.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        assertNotNull(adminResponse.getBody());
        assertFalse(adminResponse.getBody().isEmpty());

        CustomPageImpl<UserResponse> users = adminResponse.getBody();

        Set<String> usernames = users.getContent().stream()
                .map(UserResponse::username)
                .collect(Collectors.toSet());

        assertEquals(Set.of("username7", "username10", "adminUser"), usernames);
    }

    @Test
    void updateMe() {
        ResponseEntity<Void> response = userClient.updateMe(
                userToken,
                new UserFullRequest("username8", "email7@gmail.com","securePassword123")
        );

        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(204)));

        UUID userId = UUID.fromString(decoder.decode(userToken).getSubject());
        assertNotNull(userId);

        /*
         * Проверяем фактическое состояние БД,
         * а не только HTTP-статус ответа.
         *
         * Это подтверждает успешное выполнение
         * бизнес-операции на уровне persistence слоя.
         */
        jdbcTemplate.query(
                "SELECT * FROM \"user\" WHERE id = :p_id",
                new MapSqlParameterSource("p_id", userId),
                rs -> {
                    assertEquals("username8", rs.getString("username"));
                    assertEquals("email7@gmail.com", rs.getString("email"));
                }
        );
    }

    @Test
    void delete() {
        String token = authClient.register("username11", "email11@gmail.com","securePassword123");

        ResponseEntity<Void> response = userClient.deleteMe(token);

        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(204)));

        val users = jdbcTemplate.query(
                "SELECT * FROM \"user\" WHERE username = :username",
                new MapSqlParameterSource("username", "username11"),
                (rs, rowNum) -> rs.getString("username")
        );

        assertTrue(users.isEmpty());
    }

    @Test
    void patch() {
        ResponseEntity<Void> response = userClient.patchMe(
                userToken,
                new UserPartialRequest("username9", null,null)
        );

        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(204)));

        UUID userId = UUID.fromString(decoder.decode(userToken).getSubject());
        assertNotNull(userId);

        jdbcTemplate.query(
                "SELECT * FROM \"user\" WHERE id = :p_id",
                new MapSqlParameterSource("p_id", userId),
                rs -> {
                    assertEquals("username9", rs.getString("username"));
                    assertEquals("email7@gmail.com", rs.getString("email"));
                }
        );
    }

    @Test
    void updateRoleForUser() {
        UUID userId = jdbcTemplate.queryForObject(
                "SELECT id FROM \"user\" WHERE username = :username",
                new MapSqlParameterSource("username", "username10"),
                (rs, rowNum) -> UUID.fromString(rs.getString("id"))
        );

        ResponseEntity<Void> response = userClient.updateRole(
                userToken, userId.toString(), new RoleRequest("ADMIN"));

        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());
    }

    @Test
    void updateRoleForAdmin() {
        UUID userId = jdbcTemplate.queryForObject(
                "SELECT id FROM \"user\" WHERE username = :username",
                new MapSqlParameterSource("username", "username10"),
                (rs, rowNum) -> UUID.fromString(rs.getString("id"))
        );

        ResponseEntity<Void> response = userClient.updateRole(
                adminToken,
                userId.toString(),
                new RoleRequest("ADMIN")
        );

        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(204)));

        jdbcTemplate.query(
                "SELECT * FROM \"user\" WHERE username = :username",
                new MapSqlParameterSource("username", "username10"),
                rs -> {
                    assertEquals("username10", rs.getString("username"));
                    assertEquals("email10@gmail.com", rs.getString("email"));
                    assertEquals("ADMIN", rs.getString("role"));
                }
        );
    }
}