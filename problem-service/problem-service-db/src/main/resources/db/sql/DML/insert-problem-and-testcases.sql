DO
$$
DECLARE
generated_id UUID := gen_random_uuid();
BEGIN
INSERT INTO problem (id, title, description, constraints, difficulty, class_name, method_signature)
VALUES (generated_id, 'Add Numbers', 'Add two integers and return the sum', '1 ≤ a, b ≤ 1000', 'EASY',
        'Solution', 'public int add(int a, int b)') RETURNING id
INTO generated_id;

INSERT INTO testcase (id, input_data, expected_output, visible, problem_id)
VALUES (gen_random_uuid(), '1 2', '3', TRUE, generated_id),
       (gen_random_uuid(), '0 0', '0', TRUE, generated_id),
       (gen_random_uuid(), '-5 10', '5', FALSE, generated_id),
       (gen_random_uuid(), '1 1', '2', FALSE, generated_id),
       (gen_random_uuid(), '1000 1000', '2000', FALSE, generated_id),
       (gen_random_uuid(), '1 999', '1000', FALSE, generated_id),
       (gen_random_uuid(), '500 750', '1250', FALSE, generated_id),
       (gen_random_uuid(), '999 1', '1000', FALSE, generated_id),
       (gen_random_uuid(), '42 987', '1029', FALSE, generated_id),
       (gen_random_uuid(), '2147483647 1', '-2147483648', FALSE, generated_id),
       (gen_random_uuid(), '123 456', '579', FALSE, generated_id);
UPDATE problem
set ready_for_publish = TRUE
WHERE id = generated_id;
END $$;