package com.technokratos.submissionserviceimpl.repository;

import com.technokratos.submissionserviceapi.enums.SubmissionStatus;
import com.technokratos.submissionserviceimpl.entity.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends MongoRepository<Submission, String> {
    List<Submission> findByUserIdAndProblemId(UUID userId, UUID problemId);

    List<Submission> findAllByUserId(UUID userId);

    boolean existsByUserIdAndProblemIdAndStatus(UUID userId, UUID problemId, SubmissionStatus status);
}
