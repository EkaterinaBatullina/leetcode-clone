package com.technokratos.submissionserviceimpl.service;

import com.technokratos.submissionserviceapi.dto.request.SubmissionRequest;
import com.technokratos.submissionserviceapi.dto.response.SubmissionResponse;
import com.technokratos.submissionserviceapi.enums.SubmissionStatus;
import com.technokratos.submissionserviceimpl.entity.Submission;
import com.technokratos.submissionserviceimpl.mapper.SubmissionMapper;
import com.technokratos.submissionserviceimpl.repository.SubmissionRepository;
import com.technokratos.submissionserviceimpl.service.base.BaseSubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService implements BaseSubmissionService {
    private final SubmissionRepository repository;
    private final SubmissionMapper mapper;

    @Override
    @Cacheable(value = "submissions", key = "#id")
    public SubmissionResponse findById(String id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("submission not found with id: %s".formatted(id))));
    }

    @Override
    @Cacheable(value = "submissions", key = "#userId")
    public List<SubmissionResponse> findAllByUserId(UUID userId) {
        return mapper.toResponse(repository.findAllByUserId(userId));
    }

    @Override
    @CacheEvict(value = "submissions", key="#id")
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public String create(SubmissionRequest submissionRequest) {
        Submission submission = mapper.toEntity(submissionRequest);
        submission = repository.save(submission);
        log.debug("saved submission: {}", submission);
        return submission.getId();
    }

    @Override
    @CacheEvict(value = "submissions", key="#id")
    public void update(String id, SubmissionRequest submissionRequest) {
        Submission submission = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("submission not found with id: %s".formatted(id)));
        mapper.updateEntity(submissionRequest, submission);
        repository.save(submission);
    }

    @Override
    @Cacheable(value = "submissions", key = "T(String).format('first_success_%s_%s', #userId, #problemId)")
    public boolean isFirstSuccessfulAttempt(UUID userId, UUID problemId) {
        return !repository.existsByUserIdAndProblemIdAndStatus(userId, problemId, SubmissionStatus.SOLVED);
    }

}
