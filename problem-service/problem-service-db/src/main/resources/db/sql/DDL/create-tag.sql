CREATE TABLE tag
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE problem_tag
(
    problem_id UUID REFERENCES problem (id) ON DELETE CASCADE,
    tag_id     UUID REFERENCES tag (id) ON DELETE CASCADE,
    PRIMARY KEY (problem_id, tag_id)
);
