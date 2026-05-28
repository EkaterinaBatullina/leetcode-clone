CREATE TABLE problem
(
    id                UUID PRIMARY KEY,
    title             VARCHAR(255) NOT NULL,
    description       TEXT         NOT NULL,
    constraints       TEXT         NOT NULL,
    difficulty        VARCHAR      NOT NULL CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    ready_for_publish BOOLEAN      DEFAULT FALSE,
    publish_status    VARCHAR      NOT NULL DEFAULT 'NOT_PUBLISHED' CHECK (publish_status IN ('NOT_PUBLISHED', 'PUBLISHING', 'PUBLISHED', 'FAILED')),
    updated_at        TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);
