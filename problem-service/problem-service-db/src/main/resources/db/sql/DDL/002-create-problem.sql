CREATE TABLE problem
(
    id                UUID PRIMARY KEY,
    title             VARCHAR(255)   NOT NULL,
    description       TEXT           NOT NULL,
    constraints       TEXT           NOT NULL,
    difficulty        DIFFICULTY     NOT NULL,
    class_name        VARCHAR(100)   NOT NULL,
    method_name       VARCHAR(100)   NOT NULL,
    method_signature  VARCHAR(255)   NOT NULL,
    ready_for_publish BOOLEAN        NOT NULL DEFAULT FALSE,
    publish_status    PUBLISH_STATUS NOT NULL DEFAULT 'NOT_PUBLISHED',
    updated_at        TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);
