package com.technokratos.problemserviceimpl.service.base;

import com.technokratos.problemserviceapi.dto.request.ProblemRequest;
import com.technokratos.problemserviceapi.dto.request.PublishProblemsRequest;
import com.technokratos.problemserviceapi.dto.request.RunRequest;
import com.technokratos.problemserviceapi.dto.response.ProblemResponse;
import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.problemserviceapi.enums.PublishStatus;
import com.technokratos.problemserviceimpl.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BaseProblemService {
    ProblemResponse findById(UUID id);

    List<ProblemResponse> getAll();

<<<<<<< HEAD
    Page<ProblemResponse> getAllWithPagination(List<Difficulty> difficulty, List<String> category, List<String> tag, Pageable pageable);
=======
    Page<ProblemResponse> getAllWithPagination(List<Difficulty> difficulty, List<String> tag, Pageable pageable);
>>>>>>> feature/problem-and-submission-service

    void deleteById(UUID id);

    UUID create(ProblemRequest userRequest);

    void replace(UUID id, ProblemRequest userRequest);

    void update(UUID id, ProblemRequest userRequest);


    void run(RunRequest request);

    void submit(RunRequest request);

    void markAsFailed(UUID id);

    void markAsPublished(UUID id);

    List<Problem> getAllByReadyForPublish();

    PublishProblemsRequest findAllByReadyForPublish();

    void publishProblems();

    void publishTestcases(UUID problemId);

    PublishStatus getPublishStatus(UUID id);

    Difficulty getDifficulty(UUID id);
}
