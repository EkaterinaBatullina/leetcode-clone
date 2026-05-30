package com.technokratos.repository;

import com.technokratos.dto.enums.Status;
import com.technokratos.model.OutboxEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {
    /*
     * Сохранение события в Outbox в рамках бизнес-транзакции.
     * Фактическая отправка в Kafka выполняется асинхронно планировщиком.
     */
    private static final String SQL_INSERT_EVENT = """
        INSERT INTO outbox_events (
            id,
            aggregate_id,
            type,
            payload,
            topic,
            status
        )
        VALUES (?, ?, ?, ?::jsonb, ?, ?)
        """;

    /*
     * Фиксация результата обработки события.
     * updated_at используется для восстановления зависших записей.
     */
    private static final String SQL_UPDATE_STATUS = """
        UPDATE outbox_events
        SET status = ?,
            updated_at = NOW()
        WHERE id = ?
        """;

    /*
     * Атомарно переводит события из NEW в PROCESSING и возвращает их
     * для отправки. FOR UPDATE SKIP LOCKED исключает конкурентную
     * обработку одной записи несколькими инстансами сервиса.
     */
    private static final String SQL_POLL_AND_LOCK_EVENTS = """
        UPDATE outbox_events
        SET status = 'PROCESSING',
            updated_at = NOW()
        WHERE id IN (
            SELECT id
            FROM outbox_events
            WHERE status = 'NEW'
            ORDER BY created_at ASC
            FOR UPDATE SKIP LOCKED
            LIMIT ?
        )
        RETURNING
            id,
            aggregate_id,
            type,
            payload,
            topic,
            status,
            created_at,
            updated_at
        """;

    /*
     * Возвращает зависшие PROCESSING-события в повторную обработку.
     *
     * После нескольких неудачных попыток событие переводится в FAILED
     * для предотвращения бесконечных циклов переотправки.
     */
    private static final String SQL_RESET_STUCK_EVENTS = """
        UPDATE outbox_events
        SET status = CASE 
                        WHEN attempts >= 3 THEN 'FAILED'
                        ELSE 'NEW'
                     END,
            attempts = attempts + 1,
            updated_at = NOW()
        WHERE status = 'PROCESSING'
          AND updated_at < NOW() - INTERVAL '5 minutes'
    """;

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<OutboxEventEntity> rowMapper = (rs, rowNum) ->
            OutboxEventEntity.builder()
                    .id(rs.getObject("id", UUID.class))
                    .aggregateId(rs.getString("aggregate_id"))
                    .type(rs.getString("type"))
                    .payload(rs.getString("payload"))
                    .topic(rs.getString("topic"))
                    .status(Status.valueOf(rs.getString("status")))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                    .build();

    @Override
    public void save(OutboxEventEntity entity) {
        jdbcTemplate.update(
                SQL_INSERT_EVENT,
                entity.getId(),
                entity.getAggregateId(),
                entity.getType(),
                entity.getPayload(),
                entity.getTopic(),
                entity.getStatus().name()
        );
    }

    @Override
    public List<OutboxEventEntity> pollAndLock(int limit) {
        return jdbcTemplate.query(
                SQL_POLL_AND_LOCK_EVENTS,
                rowMapper,
                limit
        );
    }

    @Override
    public void updateStatus(UUID id, Status status) {
        jdbcTemplate.update(
                SQL_UPDATE_STATUS,
                status.name(),
                id
        );
    }

    @Override
    public void resetStuckProcessingEvents() {
        jdbcTemplate.update(SQL_RESET_STUCK_EVENTS);
    }
}