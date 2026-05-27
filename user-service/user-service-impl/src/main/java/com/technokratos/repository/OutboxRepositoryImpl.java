package com.technokratos.repository;

import com.technokratos.dto.enums.Status;
import com.technokratos.model.OutboxEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {
    private static final String SQL_INSERT_EVENT = "INSERT INTO outbox_events (id, aggregate_id, type, payload, topic, status) VALUES (?, ?, ?, ?::jsonb, ?, ?)";
    private static final String SQL_GET_NEW_EVENTS = "SELECT * FROM outbox_events WHERE status = 'NEW' FOR UPDATE SKIP LOCKED LIMIT ?";
    private static final String SQL_UPDATE_STATUS = "UPDATE outbox_events SET status = ? WHERE id = ?";
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<OutboxEventEntity> rowMapper = (rs, rowNum) -> OutboxEventEntity.builder()
            .id(rs.getObject("id", UUID.class))
            .aggregateId(rs.getString("aggregate_id"))
            .type(rs.getString("type"))
            .payload(rs.getString("payload"))
            .topic(rs.getString("topic"))
            .status(Status.valueOf(rs.getString("status")))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .build();

    @Override
    public void save(OutboxEventEntity entity) {
        jdbcTemplate.update(SQL_INSERT_EVENT,
                entity.getId(),
                entity.getAggregateId(),
                entity.getType(),
                entity.getPayload(),
                entity.getTopic(),
                entity.getStatus().name()
        );
    }

    @Override
    @Transactional
    public List<OutboxEventEntity> findAllNew(int limit) {
        return jdbcTemplate.query(SQL_GET_NEW_EVENTS, rowMapper, limit);
    }

    @Override
    public void updateStatus(UUID id, Status status) {
        jdbcTemplate.update(SQL_UPDATE_STATUS, status.name(), id);
    }
}
