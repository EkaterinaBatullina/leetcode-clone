CREATE TABLE testcase
(
    id              UUID PRIMARY KEY,
    input_data      TEXT    NOT NULL,
    expected_output TEXT    NOT NULL,
    cpu_time_limit  INT     NOT NULL DEFAULT 2,
    memory_limit    INT     NOT NULL DEFAULT 65536,
    visible         BOOLEAN NOT NULL DEFAULT FALSE,
    problem_id      UUID REFERENCES problem (id) ON DELETE CASCADE
);
