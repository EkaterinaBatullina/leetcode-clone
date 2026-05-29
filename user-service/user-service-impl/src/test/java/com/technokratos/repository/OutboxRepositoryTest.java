package com.technokratos.repository;

import com.technokratos.dto.enums.Status;
import com.technokratos.model.OutboxEventEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(OutboxRepositoryImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class OutboxRepositoryTest {
    @Autowired
    OutboxRepositoryImpl outboxRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void save() {
        UUID eventId = UUID.randomUUID();

        OutboxEventEntity entity = OutboxEventEntity.builder()
                .id(eventId)
                .aggregateId("agg-1")
                .type("TEST_TYPE")
                .payload("{\"key\":\"val\"}")
                .topic("test-topic")
                .status(Status.NEW)
                .build();

        outboxRepository.save(entity);

        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT id, status, payload, topic, aggregate_id FROM outbox_events WHERE id = ?",
                eventId
        );

        assertEquals(eventId, result.get("id"));
        assertEquals("NEW", result.get("status"));
        assertEquals("test-topic", result.get("topic"));
        assertEquals("agg-1", result.get("aggregate_id"));
        assertNotNull(result.get("payload"));
    }

    @Test
    void pollAndLock_marksAsProcessing_andReturnsEvents() {
        UUID eventId = UUID.randomUUID();

        jdbcTemplate.update("""
                INSERT INTO outbox_events (id, aggregate_id, type, payload, topic, status, created_at, updated_at)
                VALUES (?, 'agg-2', 'LOCK_TEST', '{}', 'topic', 'NEW', now(), now())
                """, eventId);

        List<OutboxEventEntity> events = outboxRepository.pollAndLock(10);

        assertFalse(events.isEmpty());
        assertEquals(eventId, events.get(0).getId());
        assertEquals(Status.PROCESSING, events.get(0).getStatus());

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM outbox_events WHERE id = ?",
                String.class,
                eventId
        );

        assertEquals("PROCESSING", status);
    }

    @Test
    void updateStatus() {
        UUID eventId = UUID.randomUUID();

        jdbcTemplate.update("""
                INSERT INTO outbox_events (id, aggregate_id, type, payload, topic, status, created_at, updated_at)
                VALUES (?, 'agg-3', 'UPDATE_TEST', '{}', 'topic', 'NEW', now(), now())
                """, eventId);

        outboxRepository.updateStatus(eventId, Status.SENT);

        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT status, updated_at FROM outbox_events WHERE id = ?",
                eventId
        );

        assertEquals("SENT", result.get("status"));
        assertNotNull(result.get("updated_at"));
    }
}
