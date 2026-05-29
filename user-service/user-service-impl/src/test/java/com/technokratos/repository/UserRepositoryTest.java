package com.technokratos.repository;

import com.technokratos.dto.enums.Role;
import com.technokratos.exception.UserNotFoundException;
import com.technokratos.model.UserEntity;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(UserRepositoryImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = "test")
public class UserRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserRepository repository;

    @Test
    void save() {
        String query = "SELECT id, username, email, password, role from \"user\" WHERE username = ?";
        List<UserEntity> firstResult = jdbcTemplate.query(query,
                ps -> ps.setString(1, "save"),
                (rs, rowNum) -> UserEntity.builder()
                        .uuid(UUID.fromString(rs.getString("id")))
                        .username(rs.getString("username"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .role(Role.valueOf(rs.getString("role")))
                        .build()
        );
        assertTrue(firstResult.isEmpty());

        UserEntity entity = UserEntity.builder()
                .username("save")
                .email("email")
                .password("password")
                .role(Role.USER)
                .build();
        repository.save(entity);

        List<UserEntity> secondResult = jdbcTemplate.query(query,
                ps -> ps.setString(1, "save"),
                (rs, rowNum) -> UserEntity.builder()
                        .uuid(UUID.fromString(rs.getString("id")))
                        .username(rs.getString("username"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .role(Role.valueOf(rs.getString("role")))
                        .build()
        );

        assertEquals(1, secondResult.size());
        UserEntity savedUser = secondResult.get(0);
        assertEquals("save", savedUser.getUsername());
        assertEquals("email", savedUser.getEmail());
        assertEquals("password", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());

    }

    @Test
    void update_success() {
        val insertQuery = "INSERT INTO \"user\" (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)";
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(insertQuery,
                id,
                "oldUsername",
                "old@example.com",
                "oldPassword",
                "USER");

        val checkQuery = "SELECT username, email, password FROM \"user\" WHERE id = '%s'".formatted(id);

        jdbcTemplate.query((String) checkQuery, rs -> {
            assertEquals("oldUsername", rs.getString("username"));
            assertEquals("old@example.com", rs.getString("email"));
            assertEquals("oldPassword", rs.getString("password"));
        });

        UserEntity user = UserEntity.builder()
                .uuid(id)
                .username("newUsername")
                .email("new@example.com")
                .password("newPassword")
                .role(Role.USER)
                .build();
        repository.update(user);

        jdbcTemplate.query((String) checkQuery, rs -> {
            assertEquals("newUsername", rs.getString("username"));
            assertEquals("new@example.com", rs.getString("email"));
            assertEquals("newPassword", rs.getString("password"));
        });
    }

    @Test
    void update_notFound_throwsException() {
        UUID nonExistentId = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .uuid(nonExistentId)
                .username("doesNotExist")
                .email("doesNotExist@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        assertThrows(UserNotFoundException.class, () -> repository.update(user));
    }

    @Test
    void updateRole_success() {
        String username = "username";
        String email = "email";
        String password = "password";
        Optional<UUID> uuid = jdbcTemplate.queryForStream(
                "INSERT INTO \"user\" (username, email, password, role) VALUES (?, ?, ?, ?) RETURNING id",
                ps -> {
                    ps.setString(1, username);
                    ps.setString(2, email);
                    ps.setString(3, password);
                    ps.setString(4, Role.USER.name());
                },
                (rs, rowNum) -> UUID.fromString(rs.getString("id"))).findAny();

        assertTrue(uuid.isPresent());

        UserEntity entity = UserEntity.builder()
                .uuid(uuid.get())
                .username(username)
                .email(email)
                .password(password)
                .role(Role.ADMIN)
                .build();

        repository.updateRole(entity);

        Optional<UserEntity> result = jdbcTemplate.queryForStream(
                "SELECT id, username, email, password, role FROM \"user\" WHERE id = ?",
                ps -> ps.setObject(1, uuid.get()),
                (rs, rowNum) -> UserEntity.builder()
                            .uuid(UUID.fromString(rs.getString("id")))
                            .username(rs.getString("username"))
                            .email(rs.getString("email"))
                            .password(rs.getString("password"))
                            .role(Role.valueOf(rs.getString("role")))
                            .build()).findAny();

        assertTrue(result.isPresent());
        UserEntity resultEntity = result.get();
        assertEquals(username, resultEntity.getUsername());
        assertEquals(email, resultEntity.getEmail());
        assertEquals(password, resultEntity.getPassword());
        assertEquals(Role.ADMIN, resultEntity.getRole());
    }

    @Test
    void updateRole_notFound_throwsException() {
        UUID nonExistentId = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .uuid(nonExistentId)
                .role(Role.ADMIN)
                .build();

        assertThrows(UserNotFoundException.class, () -> repository.updateRole(user));
    }

    @Test
    void deleteById_success() {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update("INSERT INTO \"user\" (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)",
                id, "deleteUsername", "delete@example.com", "password", "USER");

        val resultFirst = jdbcTemplate
                .queryForList("SELECT id FROM \"user\" WHERE id = '%s'".formatted(id));
        assertEquals(1, resultFirst.size());

        repository.deleteById(id);

        val resultSecond = jdbcTemplate
                .queryForList("SELECT id FROM \"user\" WHERE id = '%s'".formatted(id));
        assertTrue(resultSecond.isEmpty());
    }

    @Test
    void deleteById_notFound_throwsException() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(UserNotFoundException.class, () -> repository.deleteById(nonExistentId));
    }

    @Test
    void deleteByUsername_success() {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update("INSERT INTO \"user\" (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)",
                id, "deleteUsername", "delete@example.com", "password", "USER");

        val resultFirst = jdbcTemplate
                .queryForList("SELECT id FROM \"user\" WHERE username = 'deleteUsername'");
        assertEquals(1, resultFirst.size());

        repository.deleteByUsername("deleteUsername");

        val resultSecond = jdbcTemplate
                .queryForList("SELECT id FROM \"user\" WHERE username = 'deleteUsername'");
        assertTrue(resultSecond.isEmpty());
    }

    @Test
    void deleteByUsername_notFound_throwsException() {
        String username = "nonExistent";
        assertThrows(UserNotFoundException.class, () -> repository.deleteByUsername(username));
    }

    @Test
    void findById() {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update("INSERT INTO \"user\" (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)",
                id, "find", "find@example.com", "password", "USER");

        val user = repository.findById(id);

        assertTrue(user.isPresent());
        assertEquals("find", user.get().getUsername());
    }

    @Test
    void findByUsername() {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update("INSERT INTO \"user\" (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)",
                id, "findByUsername", "findByUsername@example.com", "password", "USER");

        val user = repository.findByUsername("findByUsername");

        assertTrue(user.isPresent());
        assertEquals("findByUsername@example.com", user.get().getEmail());
    }

    @Test
    void findAll() {
        UUID idFirst = UUID.randomUUID();
        UUID idSecond = UUID.randomUUID();

        jdbcTemplate.update("INSERT INTO \"user\" (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)",
                idFirst, "findFirst", "findFirst@example.com", "password", "USER");
        jdbcTemplate.update("INSERT INTO \"user\" (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)",
                idSecond, "findSecond", "findSecond@example.com", "password", "USER");

        int pageSize = 10;
        Pageable pageable = PageRequest.of(0, pageSize);

        Page<UserEntity> all = repository.findAll(pageable);

        assertTrue(all.getTotalElements() >= 2);
        assertTrue(all.getContent().size() >= 2);

        List<UUID> allUserIds = all.getContent().stream().map(UserEntity::getUuid).toList();
        assertThat(allUserIds).contains(idFirst, idSecond);

        for (UserEntity user : all.getContent()) {
            if (user.getUuid().equals(idFirst)) {
                assertEquals("findFirst", user.getUsername());
                assertEquals("findFirst@example.com", user.getEmail());
            } else if (user.getUuid().equals(idSecond)) {
                assertEquals("findSecond", user.getUsername());
                assertEquals("findSecond@example.com", user.getEmail());
            }
        }
    }
}