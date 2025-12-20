package com.technokratos.repository;

import com.technokratos.dto.enums.Role;
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
    private final static String SQL_GET_BY_ID = "SELECT * FROM \"user\" WHERE id = ?";
    private final static String SQL_GET_BY_USERNAME = "SELECT * FROM \"user\" WHERE username = ?";
    private final static String SQL_GET_BY_EMAIL = "SELECT * FROM \"user\" WHERE email = ?";
    private static final String SQL_GET_ALL_PAGED = "SELECT * FROM \"user\" ORDER BY username LIMIT ? OFFSET ?";
    private static final String SQL_GET_COUNT = "SELECT COUNT(*) FROM \"user\"";
    private final static String SQL_INSERT_USER = "INSERT INTO \"user\" (username, email, password, role) VALUES (?, ?, ?, ?) RETURNING id";
    private final static String SQL_UPDATE_USER = "UPDATE \"user\" SET username = ?, email = ?, password = ? WHERE id = ?";
    private final static String SQL_UPDATE_USER_ROLE = "UPDATE \"user\" SET role = ? WHERE id = ?";
    private final static String SQL_DELETE_USER_BY_ID = "DELETE FROM \"user\" WHERE id = ?";
    private final static String SQL_DELETE_USER_BY_USERNAME = "DELETE FROM \"user\" WHERE username = ?";

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
            return stream.findAny();
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (val stream = jdbcTemplate.queryForStream(SQL_GET_BY_USERNAME, rowMapper, username)) {
            return stream.findAny();
        }
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        try (val stream = jdbcTemplate.queryForStream(SQL_GET_BY_EMAIL, rowMapper, email)) {
            return stream.findAny();
        }
    }

    @Override
    public Page<UserEntity> findAll(Pageable pageable) {
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();
        List<UserEntity> users = jdbcTemplate.query(SQL_GET_ALL_PAGED, rowMapper, limit, offset);
        Long total = jdbcTemplate.queryForObject(SQL_GET_COUNT, Long.class);
        long safeTotal = total != null ? total : 0;
        return new PageImpl<>(users, pageable, safeTotal);
    }

    @Override
    public UUID save(UserEntity userEntity) {
        return jdbcTemplate.query(
                SQL_INSERT_USER,
                ps -> {
                    ps.setString(1, userEntity.getUsername());
                    ps.setString(2, userEntity.getEmail());
                    ps.setString(3, userEntity.getPassword());
                    ps.setString(4, userEntity.getRole().name());
                },
                rs -> rs.next() ? UUID.fromString(rs.getString(1)) : null
        );
    }

    @Override
    public void update(UserEntity userEntity) {
        jdbcTemplate.update(SQL_UPDATE_USER,
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getUuid()
        );
    }

    @Override
    public void updateRole(UserEntity userEntity) {
        jdbcTemplate.update(SQL_UPDATE_USER_ROLE,
                userEntity.getRole().name(),
                userEntity.getUuid()
        );
    }

    @Override
    public void deleteById(UUID uuid) {
        jdbcTemplate.update(SQL_DELETE_USER_BY_ID, uuid);
    }

    @Override
    public void deleteByUsername(String username) {
        jdbcTemplate.update(SQL_DELETE_USER_BY_USERNAME, username);
    }
}