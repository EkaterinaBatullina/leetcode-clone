package com.technokratos.repository;

import com.technokratos.enums.Difficulty;
import com.technokratos.enums.PublishStatus;
import com.technokratos.BaseRepositoryTest;
import com.technokratos.entity.Problem;
import com.technokratos.repository.ProblemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProblemRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllByReadyForPublish_ReturnsReadyProblems() {
        List<Problem> result = problemRepository.findAllByReadyForPublish();
        int size = result.size();
        Problem readyProblem = Problem.builder()
                .title("Ready Problem")
                .description("Description")
                .constraints("Constraints")
                .difficulty(Difficulty.MEDIUM)
                .readyForPublish(true)
                .publishStatus(PublishStatus.NOT_PUBLISHED)
                .updatedAt(LocalDateTime.now())
                .build();
        problemRepository.save(readyProblem);

        Problem nonReadyProblem = Problem.builder()
                .title("Non-Ready Problem")
                .description("Description")
                .constraints("Constraints")
                .difficulty(Difficulty.EASY)
                .readyForPublish(false)
                .publishStatus(PublishStatus.NOT_PUBLISHED)
                .updatedAt(LocalDateTime.now())
                .build();
        problemRepository.save(nonReadyProblem);

        // Flush and clear to simulate a fresh query
        entityManager.flush();
        entityManager.clear();

        // Execute query
        result = problemRepository.findAllByReadyForPublish();
        // Verify results
        assertEquals(size+1, result.size());
        assertEquals(readyProblem.getId(), result.get(size).getId());
        assertTrue(result.get(size).getReadyForPublish());
        assertNotEquals(PublishStatus.PUBLISHED, result.get(result.size()- 1).getPublishStatus());
    }

    @Test
    void markAsPublished_UpdatesStatusSuccessfully() {
        Problem problem = Problem.builder()
                .title("Problem")
                .description("Description")
                .constraints("Constraints")
                .difficulty(Difficulty.HARD)
                .publishStatus(PublishStatus.NOT_PUBLISHED)
                .build();
        problem = problemRepository.save(problem);
        UUID id = problem.getId();

        // Execute update
        problemRepository.markAsPublished(id);

        // Flush and clear to reload from DB
        entityManager.flush();
        entityManager.clear();

        // Verify update
        Problem updated = problemRepository.findById(id).orElseThrow();
        assertEquals(PublishStatus.PUBLISHED, updated.getPublishStatus());
    }

    @Test
    void markAsFailed_UpdatesStatusSuccessfully() {
        Problem problem = Problem.builder()
                .title("Problem")
                .description("Description")
                .constraints("Constraints")
                .difficulty(Difficulty.EASY)
                .publishStatus(PublishStatus.NOT_PUBLISHED)
                .build();
        problem = problemRepository.save(problem);
        UUID id = problem.getId();

        // Execute update
        problemRepository.markAsFailed(id);

        // Flush and clear to reload from DB
        entityManager.flush();
        entityManager.clear();

        // Verify update
        Problem updated = problemRepository.findById(id).orElseThrow();
        assertEquals(PublishStatus.FAILED, updated.getPublishStatus());
    }

    @Test
    void findDifficultyById_ReturnsCorrectValue() {
        Problem problem = Problem.builder()
                .title("Problem")
                .description("Description")
                .constraints("Constraints")
                .difficulty(Difficulty.HARD)
                .publishStatus(PublishStatus.NOT_PUBLISHED)
                .build();
        problem = problemRepository.save(problem);
        UUID id = problem.getId();

        Optional<Difficulty> result = problemRepository.findDifficultyById(id);
        assertTrue(result.isPresent());
        assertEquals(Difficulty.HARD, result.get());
    }

    @Test
    void findPublishStatusById_ReturnsCorrectValue() {
        Problem problem = Problem.builder()
                .title("Problem")
                .description("Description")
                .constraints("Constraints")
                .difficulty(Difficulty.EASY)
                .publishStatus(PublishStatus.FAILED)
                .build();
        problem = problemRepository.save(problem);
        UUID id = problem.getId();

        Optional<PublishStatus> result = problemRepository.findPublishStatusById(id);
        assertTrue(result.isPresent());
        assertEquals(PublishStatus.FAILED, result.get());
    }
}