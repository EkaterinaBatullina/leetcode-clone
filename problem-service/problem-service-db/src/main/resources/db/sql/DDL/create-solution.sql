CREATE TABLE solution
(
    id          UUID PRIMARY KEY,
    problem_id  UUID REFERENCES problem (id) ON DELETE CASCADE,
    type        solution_type NOT NULL,
    content     TEXT          NOT NULL,
    language_id INTEGER REFERENCES language (id)
);
