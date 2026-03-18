package com.technokratos.problemserviceimpl.service;

import com.technokratos.problemserviceapi.dto.request.TestcaseRequest;
import com.technokratos.problemserviceapi.dto.response.TestcaseResponse;
import com.technokratos.problemserviceimpl.BaseServiceTest;
import com.technokratos.problemserviceimpl.entity.Problem;
import com.technokratos.problemserviceimpl.entity.Testcase;
import com.technokratos.problemserviceimpl.mapper.TestcaseMapper;
import com.technokratos.problemserviceimpl.repository.ProblemRepository;
import com.technokratos.problemserviceimpl.repository.TestcaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestcaseServiceTest extends BaseServiceTest {

    @Autowired
    private TestcaseService testcaseService;

    @MockitoBean
    private TestcaseRepository testcaseRepository;

    @MockitoBean
    private ProblemRepository problemRepository;

    @MockitoBean
    private TestcaseMapper testcaseMapper;

    @MockitoBean
    private CacheManager cacheManager;

    @BeforeEach
    void setUpCache() {
        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("testcases")).thenReturn(mockCache);
    }

    @Test
    void findById() {
        UUID testcaseId = UUID.randomUUID();
        Testcase testcase = new Testcase();
        TestcaseResponse response = new TestcaseResponse(
                testcaseId, "input", "output", true, 1000, 512
        );

        when(testcaseRepository.findById(testcaseId)).thenReturn(Optional.of(testcase));
        when(testcaseMapper.toResponse(testcase)).thenReturn(response);

        TestcaseResponse result = testcaseService.findById(testcaseId);

        assertEquals(response, result);
        verify(testcaseRepository).findById(testcaseId);
        verify(testcaseMapper).toResponse(testcase);
    }

    @Test
    void getAllByProblemId() {
        UUID problemId = UUID.randomUUID();

        // 1. Verify problem exists
        when(problemRepository.existsById(problemId)).thenReturn(true);

        // 2. Setup testcase data
        Testcase testcase = new Testcase();
        List<Testcase> testcases = List.of(testcase);
        TestcaseResponse response = new TestcaseResponse(
                UUID.randomUUID(), "input", "output", true, 1000, 512
        );

        // 3. Mock repository responses - return list of entities
        when(testcaseRepository.findAllByProblemId(problemId)).thenReturn(testcases);

        // Fix: Mock mapper to accept LIST of entities
        when(testcaseMapper.toResponse(testcases)).thenReturn(List.of(response));

        // 4. Execute service method
        List<TestcaseResponse> results = testcaseService.getAllByProblemId(problemId);

        // 5. Verify results
        assertEquals(1, results.size());
        assertEquals(response, results.get(0));
        verify(testcaseRepository).findAllByProblemId(problemId);

        // Fix: Verify mapper called with LIST of entities
        verify(testcaseMapper).toResponse(testcases);
    }

    @Test
    void deleteById() {
        UUID testcaseId = UUID.randomUUID();
        testcaseService.deleteById(testcaseId);
        verify(testcaseRepository).deleteById(testcaseId);
    }

    @Test
    void create() {
        TestcaseRequest request = new TestcaseRequest(
                "input", "output", 1000, true, 1000, 512
        );
        Problem problem = new Problem();
        Testcase testcase = new Testcase();
        UUID testcaseId = UUID.randomUUID();

        when(problemRepository.findById(any())).thenReturn(Optional.of(problem));
        when(testcaseMapper.toEntity(request)).thenReturn(testcase);
        when(testcaseRepository.save(testcase)).thenAnswer(inv -> {
            testcase.setId(testcaseId);
            return testcase;
        });

        UUID result = testcaseService.create(request);

        assertEquals(testcaseId, result);
        verify(testcaseRepository).save(testcase);
        verify(testcaseMapper).toEntity(request);
    }

    @Test
    void replace() {
        UUID testcaseId = UUID.randomUUID();
        TestcaseRequest request = new TestcaseRequest(
                "input", "output", 1000, true, 1000, 512
        );
        Testcase testcase = mock(Testcase.class);

        when(testcaseMapper.toEntity(request)).thenReturn(testcase);

        testcaseService.replace(testcaseId, request);

        verify(testcase).setId(testcaseId);
        verify(testcaseRepository).save(testcase);
    }

    @Test
    void update() {
        UUID testcaseId = UUID.randomUUID();
        TestcaseRequest request = new TestcaseRequest(
                "input", "output", 1000, true, 1000, 512
        );
        Testcase testcase = new Testcase();

        when(testcaseRepository.findById(testcaseId)).thenReturn(Optional.of(testcase));

        testcaseService.update(testcaseId, request);

        verify(testcaseMapper).updateEntity(request, testcase);
        verify(testcaseRepository).save(testcase);
    }
}