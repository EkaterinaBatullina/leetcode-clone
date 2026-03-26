package com.technokratos.repository;

import com.technokratos.dto.enums.Role;
import com.technokratos.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import com.technokratos.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_BY_ID = "SELECT id, username, email, password, role FROM \"user\" WHERE id = ?";
    private static final String SQL_GET_BY_USERNAME = "SELECT id, username, email, password, role FROM \"user\" WHERE username = ?";
    private static final String SQL_GET_BY_EMAIL = "SELECT id, username, email, password, role FROM \"user\" WHERE email = ?";
    private static final String SQL_GET_ALL_PAGED = "SELECT id, username, email, password, role FROM \"user\" ORDER BY username LIMIT ? OFFSET ?";
    private static final String SQL_GET_COUNT = "SELECT COUNT(*) FROM \"user\"";
    private static final String SQL_INSERT_USER = "INSERT INTO \"user\" (username, email, password, role) VALUES (?, ?, ?, ?) RETURNING id";
    private static final String SQL_UPDATE_USER = "UPDATE \"user\" SET username = ?, email = ?, password = ? WHERE id = ?";
    private static final String SQL_UPDATE_USER_ROLE = "UPDATE \"user\" SET role = ? WHERE id = ?";
    private static final String SQL_DELETE_USER_BY_ID = "DELETE FROM \"user\" WHERE id = ?";
    private static final String SQL_DELETE_USER_BY_USERNAME = "DELETE FROM \"user\" WHERE username = ?";

    private final RowMapper<UserEntity> rowMapper = (rs, rowNum) -> UserEntity.builder()
            .uuid(rs.getObject("id", UUID.class))
            .username(rs.getString("username"))
            .email(rs.getString("email"))
            .password(rs.getString("password"))
            .role(Role.valueOf(rs.getString("role")))
            .build();

    @Override
    public Optional<UserEntity> findById(UUID uuid) {
        try (val stream = jdbcTemplate.queryForStream(SQL_GET_BY_ID, rowMapper, uuid)) {
            return stream.findFirst();
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (val stream = jdbcTemplate.queryForStream(SQL_GET_BY_USERNAME, rowMapper, username)) {
            return stream.findFirst();
        }
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        try (val stream = jdbcTemplate.queryForStream(SQL_GET_BY_EMAIL, rowMapper, email)) {
            return stream.findFirst();
        }
    }

    @Override
    public Page<UserEntity> findAll(Pageable pageable) {
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();
        List<UserEntity> users = jdbcTemplate.query(SQL_GET_ALL_PAGED, rowMapper, limit, offset);
        long total = Objects.requireNonNull(
                jdbcTemplate.queryForObject(SQL_GET_COUNT, Long.class)
        );
        return new PageImpl<>(users, pageable, total);
    }

    @Override
    public UUID save(UserEntity userEntity) {
        return jdbcTemplate.queryForObject(
                SQL_INSERT_USER,
                UUID.class,
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getRole().name()
        );
    }

    @Override
    public void update(UserEntity userEntity) {
        int updated = jdbcTemplate.update(SQL_UPDATE_USER,
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getUuid()
        );
        if (updated == 0) {
            throw new UserNotFoundException(userEntity.getUuid());
        }
    }

    @Override
    public void updateRole(UserEntity userEntity) {
        int updated = jdbcTemplate.update(SQL_UPDATE_USER_ROLE,
                userEntity.getRole().name(),
                userEntity.getUuid()
        );
        if (updated == 0) {
            throw new UserNotFoundException(userEntity.getUuid());
        }
    }

    @Override
    public void deleteById(UUID uuid) {
        int updated = jdbcTemplate.update(SQL_DELETE_USER_BY_ID, uuid);
        if (updated == 0) {
            throw new UserNotFoundException(uuid);
        }
    }

    @Override
    public void deleteByUsername(String username) {
        int updated = jdbcTemplate.update(SQL_DELETE_USER_BY_USERNAME, username);
        if (updated == 0) {
            throw new UserNotFoundException(username);
        }
    }
}