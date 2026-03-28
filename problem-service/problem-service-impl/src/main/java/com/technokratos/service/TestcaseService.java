package com.technokratos.service;

import com.technokratos.dto.request.TestcaseRequest;
import com.technokratos.dto.response.TestcaseResponse;
import com.technokratos.entity.Testcase;
import com.technokratos.mapper.TestcaseMapper;
import com.technokratos.repository.TestcaseRepository;
import com.technokratos.service.base.BaseTestcaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class TestcaseService implements BaseTestcaseService {
    private final TestcaseRepository repository;
    private final TestcaseMapper mapper;

    @Override
    @Cacheable(value = "testcases", key = "#id")
    public TestcaseResponse findById(UUID id) {
        Testcase testcase = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("user not found with id: %s".formatted(id)));
        return mapper.toResponse(testcase);
    }

    @Override
    @Cacheable(value = "testcases", key = "#problemId")
    public List<TestcaseResponse> getAllByProblemId(UUID problemId) {
        return mapper.toResponse(repository.findAllByProblemId(problemId));
    }


    @Override
    @CacheEvict(value = "testcases", key = "#id")
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public UUID create(TestcaseRequest testcaseRequest) {
        Testcase testcase = mapper.toEntity(testcaseRequest);
        testcase = repository.save(testcase);
        return testcase.getId();
    }

    @Override
    public void replace(UUID id, TestcaseRequest testcaseRequest) {
        Testcase testcase = mapper.toEntity(testcaseRequest);
        testcase.setId(id);
        repository.save(testcase);
    }

    @Override
    @CacheEvict(value = "testcases", key = "#id")
    public void update(UUID id, TestcaseRequest testcaseRequest) {
        Testcase testcase = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("testcase not found with id: %s".formatted(id)));
        mapper.updateEntity(testcaseRequest, testcase);
        repository.save(testcase);
    }
}
