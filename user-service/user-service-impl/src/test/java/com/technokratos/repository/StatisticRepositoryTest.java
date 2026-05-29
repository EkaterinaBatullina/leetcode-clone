package com.technokratos.repository;

import com.technokratos.exception.StatisticsNotFoundException;
import com.technokratos.model.StatisticEntity;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(StatisticRepositoryImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = "test")
public class StatisticRepositoryTest {
    @Autowired
    StatisticRepositoryImpl repository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void save() {
        UUID userId = UUID.randomUUID();
        val insertUserQuery = "INSERT INTO \"user\" (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertUserQuery, userId, "save", "save@example.com", "password", "USER");

        StatisticEntity statistic = StatisticEntity.builder()
                .userId(userId)
                .solvedTasks(0)
                .attempts(0)
                .easy(0)
                .medium(0)
                .hard(0)
                .successPercentage(0)
                .build();
        repository.save(statistic);

        String query = "SELECT user_id FROM statistic WHERE user_id = ?";
        UUID result = jdbcTemplate.queryForObject(query, UUID.class, userId);

        assertNotNull(result);
        assertEquals(userId, result);
    }

    @Test
    void findById() {
        UUID userId = UUID.randomUUID();
        val insertUserQuery = "INSERT INTO \"user\" (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertUserQuery, userId, "find", "find@example.com", "password", "USER");

        String insertStatisticQuery =
                "INSERT INTO statistic (user_id, total_solved_tasks, total_attempts, solved_easy_tasks, solved_medium_tasks, solved_hard_tasks, success_percentage) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertStatisticQuery, userId, 0, 0, 0, 0, 0, 0);

        Optional<StatisticEntity> result = repository.findById(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
    }

    @Test
    void update_success() {
        UUID userId = UUID.randomUUID();
        jdbcTemplate.update("INSERT INTO \"user\" (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)",
                userId, "update", "update@example.com", "password", "USER");

        StatisticEntity statistic = StatisticEntity.builder()
                .userId(userId)
                .solvedTasks(0)
                .attempts(0)
                .easy(0)
                .medium(0)
                .hard(0)
                .successPercentage(0)
                .build();
        repository.save(statistic);

        repository.update(userId, 1, 1, 0, 0);

        Map<String, Object> result = jdbcTemplate.queryForMap("SELECT total_solved_tasks, solved_easy_tasks, total_attempts, success_percentage FROM statistic WHERE user_id = ?", userId);
        assertEquals(1, result.get("total_solved_tasks"));
        assertEquals(1, result.get("solved_easy_tasks"));
        assertEquals(1, result.get("total_attempts"));
        assertEquals(100, result.get("success_percentage"));
    }

    @Test
    void update_notFound_throwsException() {
        UUID nonExistentUser = UUID.randomUUID();

        assertThrows(StatisticsNotFoundException.class,
                () -> repository.update(nonExistentUser, 1, 1, 0, 0)
        );
    }
}