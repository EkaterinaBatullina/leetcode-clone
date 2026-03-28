package com.technokratos.repository;

import com.technokratos.entity.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProblemTestcasesRepository extends MongoRepository<ProblemTestcases, String> {
    ProblemTestcases findById(UUID problemId);

    Optional<ProblemDifficulty> findProblemDifficultyById(UUID id);

    ProblemIOAndLimits findIOAndLimitsById(UUID id);

    ProblemVisibleIOAndLimits findVisibleIOAndLimitsById(UUID id);
}
