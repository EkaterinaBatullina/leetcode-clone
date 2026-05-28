CREATE TABLE wrapper
(
    id               UUID PRIMARY KEY,
    problem_id       UUID    NOT NULL,
    language_id      INTEGER NOT NULL,
    method_signature TEXT    NOT NULL,
    wrapper          TEXT,
    CONSTRAINT fk_wrapper_problem
        FOREIGN KEY (problem_id)
            REFERENCES problem (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_wrapper_language
        FOREIGN KEY (language_id)
            REFERENCES language (id)
            ON DELETE CASCADE,
    CONSTRAINT uq_wrapper_unique
        UNIQUE (problem_id, language_id)
);
