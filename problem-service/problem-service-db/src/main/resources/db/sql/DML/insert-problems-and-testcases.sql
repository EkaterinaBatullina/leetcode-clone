DO
$$
DECLARE
generated_id UUID;
BEGIN
    -- Проблема 1: Сложение чисел
    generated_id
:= gen_random_uuid();
INSERT INTO problem (id, title, description, constraints, difficulty, class_name, method_signature)
VALUES (generated_id,
        'Сложение чисел',
        'Напишите функцию для сложения двух целых чисел и возврата суммы',
        '-1000 ≤ a, b ≤ 1000',
        'EASY',
        'Solution',
        'public int add(int a, int b)');
INSERT INTO testcase (id, input_data, expected_output, visible, problem_id)
VALUES (gen_random_uuid(), '5 10', '15', TRUE, generated_id),
       (gen_random_uuid(), '-3 7', '4', TRUE, generated_id),
       (gen_random_uuid(), '0 0', '0', FALSE, generated_id),
       (gen_random_uuid(), '1000 -500', '500', FALSE, generated_id),
       (gen_random_uuid(), '-1000 -1', '-1001', FALSE, generated_id),
       (gen_random_uuid(), '999 1', '1000', FALSE, generated_id),
       (gen_random_uuid(), '42 58', '100', FALSE, generated_id),
       (gen_random_uuid(), '32767 1', '32768', FALSE, generated_id),
       (gen_random_uuid(), '-999 0', '-999', FALSE, generated_id),
       (gen_random_uuid(), '123 -456', '-333', FALSE, generated_id);
UPDATE problem
SET ready_for_publish = TRUE
WHERE id = generated_id;

-- Проблема 2: Поиск максимума
generated_id
:= gen_random_uuid();
INSERT INTO problem (id, title, description, constraints, difficulty, class_name, method_signature)
VALUES (generated_id,
        'Поиск максимума',
        'Найдите максимальный элемент в целочисленном массиве',
        '1 ≤ arr.length ≤ 1000, -10000 ≤ arr[i] ≤ 10000',
        'EASY',
        'Solution',
        'public int findMax(int[] arr)');
INSERT INTO testcase (id, input_data, expected_output, visible, problem_id)
VALUES (gen_random_uuid(), '[3,5,2]', '5', TRUE, generated_id),
       (gen_random_uuid(), '[-1,-5,-3]', '-1', TRUE, generated_id),
       (gen_random_uuid(), '[0]', '0', FALSE, generated_id),
       (gen_random_uuid(), '[10,20,30,20]', '30', FALSE, generated_id),
       (gen_random_uuid(), '[10000,-10000]', '10000', FALSE, generated_id),
       (gen_random_uuid(), '[5,5,5,5]', '5', FALSE, generated_id),
       (gen_random_uuid(), '[-10,0,10]', '10', FALSE, generated_id),
       (gen_random_uuid(), '[1,2,3,4,5,4,3,2,1]', '5', FALSE, generated_id),
       (gen_random_uuid(), '[9999]', '9999', FALSE, generated_id),
       (gen_random_uuid(), '[-42,-10,-99]', '-10', FALSE, generated_id);
UPDATE problem
SET ready_for_publish = TRUE
WHERE id = generated_id;

-- Проблема 3: Проверка палиндрома
generated_id
:= gen_random_uuid();
INSERT INTO problem (id, title, description, constraints, difficulty, class_name, method_signature)
VALUES (generated_id,
        'Палиндром',
        'Проверьте, является ли строка палиндромом (игнорируя регистр и не буквенно-цифровые символы)',
        '1 ≤ s.length ≤ 1000',
        'MEDIUM',
        'Solution',
        'public boolean isPalindrome(String s)');
INSERT INTO testcase (id, input_data, expected_output, visible, problem_id)
VALUES (gen_random_uuid(), '"radar"', 'true', TRUE, generated_id),
       (gen_random_uuid(), '"hello"', 'false', TRUE, generated_id),
       (gen_random_uuid(), '"A man a plan a canal Panama"', 'true', FALSE, generated_id),
       (gen_random_uuid(), '"12321"', 'true', FALSE, generated_id),
       (gen_random_uuid(), '"not palindrome"', 'false', FALSE, generated_id),
       (gen_random_uuid(), '""', 'true', FALSE, generated_id),
       (gen_random_uuid(), '"a"', 'true', FALSE, generated_id),
       (gen_random_uuid(), '"Was it a car or a cat I saw?"', 'true', FALSE, generated_id),
       (gen_random_uuid(), '"Palindrome"', 'false', FALSE, generated_id),
       (gen_random_uuid(), '"No lemon, no melon"', 'true', FALSE, generated_id);
UPDATE problem
SET ready_for_publish = TRUE
WHERE id = generated_id;

-- Проблема 4: Подсчет гласных
generated_id
:= gen_random_uuid();
INSERT INTO problem (id, title, description, constraints, difficulty, class_name, method_signature)
VALUES (generated_id,
        'Подсчет гласных',
        'Подсчитайте количество гласных букв (a, e, i, o, u) в строке',
        '1 ≤ s.length ≤ 1000',
        'EASY',
        'Solution',
        'public int countVowels(String s)');
INSERT INTO testcase (id, input_data, expected_output, visible, problem_id)
VALUES (gen_random_uuid(), '"hello"', '2', TRUE, generated_id),
       (gen_random_uuid(), '"xyz"', '0', TRUE, generated_id),
       (gen_random_uuid(), '"AEIOUaeiou"', '10', FALSE, generated_id),
       (gen_random_uuid(), '"Hello World"', '3', FALSE, generated_id),
       (gen_random_uuid(), '"Why?"', '0', FALSE, generated_id),
       (gen_random_uuid(), '"Programming"', '3', FALSE, generated_id),
       (gen_random_uuid(), '"A"', '1', FALSE, generated_id),
       (gen_random_uuid(), '"test CASE"', '3', FALSE, generated_id),
       (gen_random_uuid(), '"abcdefghijklmnopqrstuvwxyz"', '5', FALSE, generated_id),
       (gen_random_uuid(), '"12345"', '0', FALSE, generated_id);
UPDATE problem
SET ready_for_publish = TRUE
WHERE id = generated_id;

-- Проблема 5: Реверс строки
generated_id
:= gen_random_uuid();
INSERT INTO problem (id, title, description, constraints, difficulty, class_name, method_signature)
VALUES (generated_id,
        'Реверс строки',
        'Переверните строку задом наперёд',
        '1 ≤ s.length ≤ 1000',
        'EASY',
        'Solution',
        'public String reverseString(String s)');
INSERT INTO testcase (id, input_data, expected_output, visible, problem_id)
VALUES (gen_random_uuid(), '"abc"', 'cba', TRUE, generated_id),
       (gen_random_uuid(), '"Java"', 'avaJ', TRUE, generated_id),
       (gen_random_uuid(), '"12345"', '54321', FALSE, generated_id),
       (gen_random_uuid(), '"a"', 'a', FALSE, generated_id),
       (gen_random_uuid(), '"racecar"', 'racecar', FALSE, generated_id),
       (gen_random_uuid(), '"Hello World"', 'dlroW olleH', FALSE, generated_id),
       (gen_random_uuid(), '"♥♦♣♠"', '♠♣♦♥', FALSE, generated_id),
       (gen_random_uuid(), '"multi\nline"', 'enil\nitlum', FALSE, generated_id),
       (gen_random_uuid(), '"  spaces  "', '  secaps  ', FALSE, generated_id),
       (gen_random_uuid(), '""', '', FALSE, generated_id);
UPDATE problem
SET ready_for_publish = TRUE
WHERE id = generated_id;

-- Проблема 6: Проверка простого числа
generated_id
:= gen_random_uuid();
INSERT INTO problem (id, title, description, constraints, difficulty, class_name, method_signature)
VALUES (generated_id,
        'Простое число',
        'Определите, является ли число простым',
        '1 ≤ n ≤ 10000',
        'MEDIUM',
        'Solution',
        'public boolean isPrime(int n)');
INSERT INTO testcase (id, input_data, expected_output, visible, problem_id)
VALUES (gen_random_uuid(), '7', 'true', TRUE, generated_id),
       (gen_random_uuid(), '4', 'false', TRUE, generated_id),
       (gen_random_uuid(), '1', 'false', FALSE, generated_id),
       (gen_random_uuid(), '2', 'true', FALSE, generated_id),
       (gen_random_uuid(), '997', 'true', FALSE, generated_id),
       (gen_random_uuid(), '10000', 'false', FALSE, generated_id),
       (gen_random_uuid(), '7919', 'true', FALSE, generated_id),
       (gen_random_uuid(), '100', 'false', FALSE, generated_id),
       (gen_random_uuid(), '0', 'false', FALSE, generated_id),
       (gen_random_uuid(), '9999', 'false', FALSE, generated_id);
UPDATE problem
SET ready_for_publish = TRUE
WHERE id = generated_id;

-- Проблема 7: Факториал
generated_id
:= gen_random_uuid();
INSERT INTO problem (id, title, description, constraints, difficulty, class_name, method_signature)
VALUES (generated_id,
        'Вычисление факториала',
        'Вычислите факториал заданного числа',
        '0 ≤ n ≤ 12',
        'MEDIUM',
        'Solution',
        'public int factorial(int n)');
INSERT INTO testcase (id, input_data, expected_output, visible, problem_id)
VALUES (gen_random_uuid(), '5', '120', TRUE, generated_id),
       (gen_random_uuid(), '0', '1', TRUE, generated_id),
       (gen_random_uuid(), '1', '1', FALSE, generated_id),
       (gen_random_uuid(), '3', '6', FALSE, generated_id),
       (gen_random_uuid(), '10', '3628800', FALSE, generated_id),
       (gen_random_uuid(), '12', '479001600', FALSE, generated_id),
       (gen_random_uuid(), '6', '720', FALSE, generated_id),
       (gen_random_uuid(), '7', '5040', FALSE, generated_id),
       (gen_random_uuid(), '8', '40320', FALSE, generated_id),
       (gen_random_uuid(), '4', '24', FALSE, generated_id);
UPDATE problem
SET ready_for_publish = TRUE
WHERE id = generated_id;

-- Проблема 8: Сумма элементов массива
generated_id
:= gen_random_uuid();
INSERT INTO problem (id, title, description, constraints, difficulty, class_name, method_signature)
VALUES (generated_id,
        'Сумма элементов массива',
        'Вычислите сумму всех элементов в целочисленном массиве',
        '1 ≤ arr.length ≤ 1000, -1000 ≤ arr[i] ≤ 1000',
        'EASY',
        'Solution',
        'public int arraySum(int[] arr)');
INSERT INTO testcase (id, input_data, expected_output, visible, problem_id)
VALUES (gen_random_uuid(), '[1,2,3]', '6', TRUE, generated_id),
       (gen_random_uuid(), '[-1,0,1]', '0', TRUE, generated_id),
       (gen_random_uuid(), '[1000,-500]', '500', FALSE, generated_id),
       (gen_random_uuid(), '[]', '0', FALSE, generated_id),
       (gen_random_uuid(), '[10,20,30,40]', '100', FALSE, generated_id),
       (gen_random_uuid(), '[-1,-2,-3]', '-6', FALSE, generated_id),
       (gen_random_uuid(), '[999]', '999', FALSE, generated_id),
       (gen_random_uuid(), '[1,1,1,1,1,1,1,1,1,1]', '10', FALSE, generated_id),
       (gen_random_uuid(), '[-1000,1000]', '0', FALSE, generated_id),
       (gen_random_uuid(), '[5,-5,10,-10]', '0', FALSE, generated_id);
UPDATE problem
SET ready_for_publish = TRUE
WHERE id = generated_id;

-- Проблема 9: Второй максимум
generated_id
:= gen_random_uuid();
INSERT INTO problem (id, title, description, constraints, difficulty, class_name, method_signature)
VALUES (generated_id,
        'Второй максимум',
        'Найдите второе по величине число в массиве',
        '2 ≤ arr.length ≤ 1000, -10000 ≤ arr[i] ≤ 10000',
        'MEDIUM',
        'Solution',
        'public int secondMax(int[] arr)');
INSERT INTO testcase (id, input_data, expected_output, visible, problem_id)
VALUES (gen_random_uuid(), '[3,1,2]', '2', TRUE, generated_id),
       (gen_random_uuid(), '[10,20,30]', '20', TRUE, generated_id),
       (gen_random_uuid(), '[5,5,4,3]', '4', FALSE, generated_id),
       (gen_random_uuid(), '[-10,-20,-5]', '-10', FALSE, generated_id),
       (gen_random_uuid(), '[100,200,300,200]', '200', FALSE, generated_id),
       (gen_random_uuid(), '[1,2]', '1', FALSE, generated_id),
       (gen_random_uuid(), '[10,10,9]', '9', FALSE, generated_id),
       (gen_random_uuid(), '[0,0,0]', '0', FALSE, generated_id),
       (gen_random_uuid(), '[9999,10000]', '9999', FALSE, generated_id),
       (gen_random_uuid(), '[-5,10,0,-15]', '0', FALSE, generated_id);
UPDATE problem
SET ready_for_publish = TRUE
WHERE id = generated_id;

-- Проблема 10: Проверка анаграмм
generated_id
:= gen_random_uuid();
INSERT INTO problem (id, title, description, constraints, difficulty, class_name, method_signature)
VALUES (generated_id,
        'Проверка анаграмм',
        'Определите, являются ли две строки анаграммами (состоят из одних и тех же букв)',
        '1 ≤ s.length, t.length ≤ 500',
        'HARD',
        'Solution',
        'public boolean isAnagram(String s, String t)');
INSERT INTO testcase (id, input_data, expected_output, visible, problem_id)
VALUES (gen_random_uuid(), '"listen" "silent"', 'true', TRUE, generated_id),
       (gen_random_uuid(), '"hello" "world"', 'false', TRUE, generated_id),
       (gen_random_uuid(), '"anagram" "nagaram"', 'true', FALSE, generated_id),
       (gen_random_uuid(), '"rat" "car"', 'false', FALSE, generated_id),
       (gen_random_uuid(), '"" ""', 'true', FALSE, generated_id),
       (gen_random_uuid(), '"a" "a"', 'true', FALSE, generated_id),
       (gen_random_uuid(), '"abc" "abcd"', 'false', FALSE, generated_id),
       (gen_random_uuid(), '"Cinema" "Iceman"', 'true', FALSE, generated_id),
       (gen_random_uuid(), '"foo" "bar"', 'false', FALSE, generated_id),
       (gen_random_uuid(), '"Dormitory" "Dirty room"', 'true', FALSE, generated_id),
       (gen_random_uuid(), '"123" "321"', 'true', FALSE, generated_id),
       (gen_random_uuid(), '"aabb" "abab"', 'true', FALSE, generated_id);
UPDATE problem
SET ready_for_publish = TRUE
WHERE id = generated_id;
END $$;