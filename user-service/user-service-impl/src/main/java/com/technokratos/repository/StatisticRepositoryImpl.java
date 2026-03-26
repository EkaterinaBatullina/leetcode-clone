package com.technokratos.repository;

import com.technokratos.exception.StatisticsNotFoundException;
import com.technokratos.model.StatisticEntity;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class StatisticRepositoryImpl implements StatisticRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_BY_ID = "SELECT * FROM statistic WHERE user_id = ?";
    private static final String SQL_INSERT_STATISTIC = "INSERT INTO statistic (user_id, total_solved_tasks, total_attempts, solved_easy_tasks, solved_medium_tasks, solved_hard_tasks, success_percentage) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_STATISTICS = """
                UPDATE statistic
                SET total_attempts = total_attempts + 1,
                total_solved_tasks = total_solved_tasks + ?,
                solved_easy_tasks = solved_easy_tasks + ?,
                solved_medium_tasks = solved_medium_tasks + ?,
                solved_hard_tasks = solved_hard_tasks + ?,
                success_percentage = (total_solved_tasks + ?) * 100 / (total_attempts + 1)
                WHERE user_id = ?
            """;

    private final RowMapper<StatisticEntity> rowMapper = (rs, rowNum) -> StatisticEntity.builder()
            .userId(rs.getObject("user_id", UUID.class))
            .solvedTasks(rs.getInt("total_solved_tasks"))
            .attempts(rs.getInt("total_attempts"))
            .easy(rs.getInt("solved_easy_tasks"))
            .medium(rs.getInt("solved_medium_tasks"))
            .hard(rs.getInt("solved_hard_tasks"))
            .successPercentage(rs.getInt("success_percentage"))
            .build();

    @Override
    public Optional<StatisticEntity> findById(UUID uuid) {
        try (val stream = jdbcTemplate.queryForStream(SQL_GET_BY_ID, rowMapper, uuid)) {
            return stream.findFirst();
        }
    }

    @Override
    public void save(StatisticEntity statisticEntity) {
        jdbcTemplate.update(SQL_INSERT_STATISTIC,
                statisticEntity.getUserId(),
                statisticEntity.getSolvedTasks(),
                statisticEntity.getAttempts(),
                statisticEntity.getEasy(),
                statisticEntity.getMedium(),
                statisticEntity.getHard(),
                statisticEntity.getSuccessPercentage()
        );
    }

    public void update(UUID userId, int solvedDelta, int easyDelta, int mediumDelta, int hardDelta) {
        int updated = jdbcTemplate.update(SQL_UPDATE_STATISTICS,
                solvedDelta,
                easyDelta,
                mediumDelta,
                hardDelta,
                solvedDelta,
                userId
        );
        if (updated == 0) {
            throw new StatisticsNotFoundException(userId);
        }
    }
}