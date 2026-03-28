package com.technokratos.repository;

import com.technokratos.enums.Difficulty;
import com.technokratos.enums.PublishStatus;
import com.technokratos.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProblemRepository extends JpaRepository<Problem, UUID>, JpaSpecificationExecutor<Problem> {
    @Query("""
            SELECT p
            FROM Problem p
            LEFT JOIN FETCH p.testcases
            WHERE p.readyForPublish = true
            AND p.publishStatus <> 'PUBLISHED'
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
