CREATE TABLE audit_log
(
    id          UUID PRIMARY KEY,
    problem_id  UUID REFERENCES problem (id) ON DELETE CASCADE,
    user_id     UUID,
    action_type VARCHAR(10) NOT NULL CHECK (action_type IN ('CREATE', 'UPDATE', 'DELETE')),
    old_data    JSONB,
    new_data    JSONB,
    timestamp   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
