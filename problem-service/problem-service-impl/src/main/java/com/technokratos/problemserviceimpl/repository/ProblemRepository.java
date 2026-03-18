package com.technokratos.problemserviceimpl.repository;

import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.problemserviceapi.enums.PublishStatus;
import com.technokratos.problemserviceimpl.entity.Problem;
<<<<<<< HEAD
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
=======
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
>>>>>>> feature/problem-and-submission-service
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

<<<<<<< HEAD
public interface ProblemRepository extends JpaRepository<Problem, UUID> {

    @Query(value = """
            SELECT * FROM problem p
            WHERE (:difficulties IS NULL OR p.difficulty = ANY(:difficulties))
            AND (:categories IS NULL OR p.categories && :categories)
            AND (:tags IS NULL OR p.tags && :tags)
            """,
            countQuery = """
                    SELECT COUNT(*) FROM problem p
                    WHERE (:difficulties IS NULL OR p.difficulty = ANY(:difficulties))
                    AND (:categories IS NULL OR p.categories && :categories)
                    AND (:tags IS NULL OR p.tags && :tags)
                    """,
            nativeQuery = true)
    Page<Problem> findByFiltersPaged(
            @Param("difficulties") List<Difficulty> difficulties,
            @Param("categories") List<String> categories,
            @Param("tags") List<String> tags,
            Pageable pageable
    );

    @Query("""
                SELECT p
                FROM Problem p
                LEFT JOIN FETCH p.testcases
                WHERE p.readyForPublish = true
                AND NOT p.publishStatus = 'PUBLISHED'
=======
public interface ProblemRepository extends JpaRepository<Problem, UUID>, JpaSpecificationExecutor<Problem> {
    @Query("""
            SELECT p
            FROM Problem p
            LEFT JOIN FETCH p.testcases
            WHERE p.readyForPublish = true
            AND p.publishStatus <> 'PUBLISHED'
>>>>>>> feature/problem-and-submission-service
            """)
    List<Problem> findAllByReadyForPublish();

    @Modifying
    @Transactional
    @Query("UPDATE Problem p SET p.publishStatus = 'PUBLISHED' WHERE p.id = :id")
    void markAsPublished(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE Problem p SET p.publishStatus = 'FAILED' WHERE p.id = :id")
    void markAsFailed(@Param("id") UUID id);

    @Query("SELECT p.difficulty FROM Problem p WHERE p.id = :id")
    Optional<Difficulty> findDifficultyById(@Param("id") UUID id);

    @Query("SELECT p.publishStatus FROM Problem p WHERE p.id = :id")
    Optional<PublishStatus> findPublishStatusById(@Param("id") UUID id);
}
