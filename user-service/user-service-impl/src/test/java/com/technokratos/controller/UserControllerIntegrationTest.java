package com.technokratos.controller;

import com.technokratos.config.TestRestTemplateConfig;
import com.technokratos.config.property.OAuth2ClientProperties;
import com.technokratos.dto.CustomPageImpl;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.RoleRequest;
import com.technokratos.dto.request.UserFullRequest;
import com.technokratos.dto.request.UserPartialRequest;
import com.technokratos.dto.response.StatisticResponse;
import com.technokratos.dto.response.TokenCoupleResponse;
import com.technokratos.dto.response.UserResponse;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestRestTemplateConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegrationTest {
    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    JwtDecoder decoder;
    String userAccessToken;
    String userForDeletionAccessToken;
    String accessAdminToken;
    OAuth2ClientProperties properties;

    @BeforeAll
    void initToken() {
        testRestTemplate.getRestTemplate().setRequestFactory(
                new HttpComponentsClientHttpRequestFactory()
        );
        userAccessToken = register("username7", "email7@gmail.com","securePassword123");
        register("username10", "email10@gmail.com","securePassword123");
        accessAdminToken = loginAsAdmin();
    }

    @Test
    void getMe() {
        ResponseEntity<UserResponse> response = testRestTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                new HttpEntity<>(createBearerAuthHeaders(userAccessToken)),
                UserResponse.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        assertNotNull(response.getBody());
        assertEquals(decoder.decode(userAccessToken).getSubject(), response.getBody().uuid().toString());
    }

    @Test
    void getStatistic() {
        ResponseEntity<StatisticResponse> response = testRestTemplate.exchange(
                "/api/v1/users/me/statistic",
                HttpMethod.GET,
                new HttpEntity<>(createBearerAuthHeaders(userAccessToken)),
                StatisticResponse.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        assertNotNull(response.getBody());
        assertEquals(decoder.decode(userAccessToken).getSubject(), response.getBody().userId().toString());
    }

    @Test
    void getByUsername() {
        ResponseEntity<UserResponse> response = testRestTemplate.exchange(
                "/api/v1/users/username8",
                HttpMethod.GET,
                new HttpEntity<>(createBearerAuthHeaders(userAccessToken)),
                UserResponse.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        assertNotNull(response.getBody());
        assertEquals("username8", response.getBody().username());
    }

    @Test
    void getAllForUser() {
        ResponseEntity<CustomPageImpl<UserResponse>> userResponse = testRestTemplate.exchange(
                "/api/v1/users",
                HttpMethod.GET,
                new HttpEntity<>(createBearerAuthHeaders(userAccessToken)),
                new ParameterizedTypeReference<>() {}
        );
        assertTrue(userResponse.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(403)));
    }

    @Test
    void getAllForAdmin() {
        ResponseEntity<CustomPageImpl<UserResponse>> adminResponse = testRestTemplate.exchange(
                "/api/v1/users",
                HttpMethod.GET,
                new HttpEntity<>(createBearerAuthHeaders(accessAdminToken)),
                new ParameterizedTypeReference<>() {}
        );
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
        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.PUT,
                new HttpEntity<>(
                        new UserFullRequest("username8", "email7@gmail.com","securePassword123"),
                        createBearerAuthHeaders(userAccessToken)),
                Void.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(204)));
        UUID userId =  UUID.fromString(decoder.decode(userAccessToken).getSubject());
        assertNotNull(userId);
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
        userForDeletionAccessToken = register("username11", "email11@gmail.com","securePassword123");
        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/users/me", HttpMethod.DELETE,
                new HttpEntity<>(
                        createBearerAuthHeaders(userForDeletionAccessToken)),
                Void.class
        );
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
        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/users/me", HttpMethod.PATCH,
                new HttpEntity<>(
                        new UserPartialRequest("username9", null,null),
                        createBearerAuthHeaders(userAccessToken)),
                Void.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(204)));
        UUID userId =  UUID.fromString(decoder.decode(userAccessToken).getSubject());
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
        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/users/%s/role".formatted(userId), HttpMethod.PATCH,
                new HttpEntity<>(new RoleRequest("ADMIN"), createBearerAuthHeaders(userAccessToken)),
                Void.class
        );

        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(403)));
    }

    @Test
    void updateRoleForAdmin() {
        UUID userId = jdbcTemplate.queryForObject(
                "SELECT id FROM \"user\" WHERE username = :username",
                new MapSqlParameterSource("username", "username10"),
                (rs, rowNum) -> UUID.fromString(rs.getString("id"))
        );
        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/users/" + userId + "/role", HttpMethod.PATCH,
                new HttpEntity<>(new RoleRequest("ADMIN"), createBearerAuthHeaders(accessAdminToken)),
                Void.class
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

    private HttpHeaders createBasicAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(
                properties.getClientId(),
                properties.getClientSecret()
        );
        return headers;
    }

    private HttpHeaders createBearerAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

    private String register(String username, String email, String password) {
        ResponseEntity<TokenCoupleResponse> response = testRestTemplate.exchange(
                "/api/v1/authentication/register", HttpMethod.POST,
                new HttpEntity<>(new UserFullRequest(username, email, password),
                        createBasicAuthHeaders()),
                TokenCoupleResponse.class
        );
        return Objects.requireNonNull(response.getBody()).accessToken();
    }

    private String loginAsAdmin() {
        ResponseEntity<TokenCoupleResponse> response = testRestTemplate.exchange(
                "/api/v1/authentication/login", HttpMethod.POST,
                new HttpEntity<>(new AuthenticationRequest("adminUser", "adminPassword"),
                        createBasicAuthHeaders()),
                TokenCoupleResponse.class
        );
        return Objects.requireNonNull(response.getBody()).accessToken();
    }
}