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
@ActiveProfiles(profiles = "test")
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
                "SELECT id, status, payload FROM outbox_events WHERE id = ?", eventId);

        assertEquals(eventId, result.get("id"));
        assertEquals("NEW", result.get("status"));
        assertNotNull(result.get("payload"));
    }

    @Test
    void findAllNew_withLocking() {
        UUID eventId = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO outbox_events (id, aggregate_id, type, payload, topic, status) VALUES (?, ?, ?, ?::jsonb, ?, ?)",
                eventId, "agg-2", "LOCK_TEST", "{}", "topic", "NEW"
        );

        List<OutboxEventEntity> events = outboxRepository.findAllNew(10);

        assertFalse(events.isEmpty());
        assertEquals(eventId, events.get(0).getId());
        assertEquals(Status.NEW, events.get(0).getStatus());
    }

    @Test
    void updateStatus() {
        UUID eventId = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO outbox_events (id, aggregate_id, type, payload, topic, status) VALUES (?, ?, ?, ?::jsonb, ?, ?)",
                eventId, "agg-3", "UPDATE_TEST", "{}", "topic", "NEW"
        );

        outboxRepository.updateStatus(eventId, Status.SENT);

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM outbox_events WHERE id = ?", String.class, eventId);

        assertEquals("SENT", status);
    }
}
