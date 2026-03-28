package com.technokratos.repository;

import com.technokratos.entity.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestcaseRepository extends JpaRepository<Testcase, UUID> {
    List<Testcase> findAllByProblemId(UUID problemId);
}