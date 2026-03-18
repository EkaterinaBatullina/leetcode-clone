package com.technokratos.problemserviceimpl.repository;

import com.technokratos.problemserviceimpl.BaseRepositoryTest;
import com.technokratos.problemserviceimpl.entity.Language;
import com.technokratos.problemserviceimpl.entity.Problem;
import com.technokratos.problemserviceimpl.entity.Wrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class WrapperRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WrapperRepository wrapperRepository;

    private Problem problem;
    private Language languageJava;
    private Language languagePython;
    private Wrapper wrapperJava;
    private Wrapper wrapperPython;

    @BeforeEach
    void setUp() {
        // Create a problem
        problem = Problem.builder()
                .title("Two Sum")
                .description("Find indices of two numbers...")
                .constraints("n == nums.length")
                .build();
        entityManager.persist(problem);

        // Create languages
        languageJava = Language.builder()
                .id(1)
                .name("Java")
                .build();
        languagePython = Language.builder()
                .id(2)
                .name("Python")
                .build();
        entityManager.persist(languageJava);
        entityManager.persist(languagePython);

        // Create wrappers
        wrapperJava = Wrapper.builder()
                .problem(problem)
                .language(languageJava)
                .methodSignature("int[] twoSum(int[] nums, int target)")
                .wrapper("public class Solution {...}")
                .build();
        wrapperPython = Wrapper.builder()
                .problem(problem)
                .language(languagePython)
                .methodSignature("def two_sum(nums, target):")
                .wrapper("class Solution: ...")
                .build();
        entityManager.persist(wrapperJava);
        entityManager.persist(wrapperPython);

        entityManager.flush();
    }

    @Test
    void findByProblemIdAndLanguageId_shouldFindWrapper() {
        // When
        Optional<Wrapper> result = wrapperRepository.findByProblemIdAndLanguageId(
                problem.getId(),
                languageJava.getId()
        );

        // Then
        assertTrue(result.isPresent());
        assertEquals(wrapperJava.getId(), result.get().getId());
        assertEquals("int[] twoSum(int[] nums, int target)", result.get().getMethodSignature());
    }

    @Test
    void findByProblemIdAndLanguageId_shouldReturnEmptyForNonExistentCombination() {
        // When
        Optional<Wrapper> result = wrapperRepository.findByProblemIdAndLanguageId(
                UUID.randomUUID(), // Non-existent problem
                languageJava.getId()
        );

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByProblemId_shouldFindAllWrappersForProblem() {
        // When
        List<Wrapper> wrappers = wrapperRepository.findAllByProblemId(problem.getId());

        // Then
        assertEquals(2, wrappers.size());
        assertTrue(wrappers.stream().anyMatch(w -> w.getId().equals(wrapperJava.getId())));
        assertTrue(wrappers.stream().anyMatch(w -> w.getId().equals(wrapperPython.getId())));
    }

    @Test
    void findAllByProblemId_shouldReturnEmptyListForNonExistentProblem() {
        // When
        List<Wrapper> wrappers = wrapperRepository.findAllByProblemId(UUID.randomUUID());

        // Then
        assertTrue(wrappers.isEmpty());
    }

    @Test
    void findAllByProblemId_shouldFetchCorrectWrapperProperties() {
        // When
        Wrapper result = wrapperRepository.findAllByProblemId(problem.getId()).get(0);

        // Then
        assertEquals(problem.getId(), result.getProblem().getId());
        assertEquals(languageJava.getId(), result.getLanguage().getId());
        assertEquals("int[] twoSum(int[] nums, int target)", result.getMethodSignature());
        assertEquals("public class Solution {...}", result.getWrapper());
    }
}