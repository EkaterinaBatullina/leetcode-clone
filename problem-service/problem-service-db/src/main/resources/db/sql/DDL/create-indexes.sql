CREATE INDEX idx_problem_difficulty ON problem (difficulty);
CREATE INDEX idx_testcase_problem_id ON testcase (problem_id);
CREATE INDEX idx_tag_name ON tag (name);