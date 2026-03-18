package com.technokratos.submissionserviceimpl.repository;

<<<<<<< HEAD
import com.technokratos.submissionserviceimpl.entity.ProblemDifficulty;
import com.technokratos.submissionserviceimpl.entity.ProblemTestcases;
=======
import com.technokratos.submissionserviceimpl.entity.*;
>>>>>>> feature/problem-and-submission-service
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProblemTestcasesRepository extends MongoRepository<ProblemTestcases, String> {
    ProblemTestcases findById(UUID problemId);
<<<<<<< HEAD
    Optional<ProblemDifficulty> findProblemDifficultyById(UUID id);
=======

    Optional<ProblemDifficulty> findProblemDifficultyById(UUID id);

    ProblemIOAndLimits findIOAndLimitsById(UUID id);

    ProblemVisibleIOAndLimits findVisibleIOAndLimitsById(UUID id);
>>>>>>> feature/problem-and-submission-service
}
