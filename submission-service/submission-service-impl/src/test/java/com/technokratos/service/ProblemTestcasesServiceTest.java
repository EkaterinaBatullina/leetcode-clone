package com.technokratos.service;

import com.technokratos.dto.request.PublishTestcasesRequest;
import com.technokratos.dto.response.TestcaseResponse;
import com.technokratos.enums.Difficulty;
import com.technokratos.entity.ProblemTestcases;
import com.technokratos.entity.Testcase;
import com.technokratos.mapper.ProblemTestcasesMapper;
import com.technokratos.repository.ProblemTestcasesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemTestcasesServiceTest {

    @Mock
    private ProblemTestcasesRepository repository;

    @Mock
    private ProblemTestcasesMapper mapper;

    @InjectMocks
    private ProblemTestcasesService service;

    @Test
    void createProblemTestcases_Success() {
        UUID problemId = UUID.randomUUID();
        PublishTestcasesRequest request = new PublishTestcasesRequest(
                problemId,
                Difficulty.MEDIUM,
                List.of(
                        new TestcaseResponse(UUID.randomUUID(), "input1", "output1", true, 100, 128),
                        new TestcaseResponse(UUID.randomUUID(), "input2", "output2", false, 200, 256)
                )
        );

        // Create Testcase objects with all fields
        List<Testcase> testcases = List.of(
                new Testcase(UUID.randomUUID(), "input1", "output1", true, 100, 128),
                new Testcase(UUID.randomUUID(), "input2", "output2", false, 200, 256)
        );

        when(mapper.toEntity(request.testcases())).thenReturn(testcases);

        // Capture the saved ProblemTestcases object
        ArgumentCaptor<ProblemTestcases> captor = ArgumentCaptor.forClass(ProblemTestcases.class);
        when(repository.save(captor.capture())).thenAnswer(inv -> {
            ProblemTestcases pt = inv.getArgument(0);
            pt.setId(problemId);
            return pt;
        });

        UUID result = service.create(request);

        // Verify ID and save call
        assertEquals(problemId, result);
        verify(repository).save(any(ProblemTestcases.class));

        // Verify computed resource limits
        ProblemTestcases saved = captor.getValue();
        assertEquals(360, saved.getCpuTimeLimit()); // (100+200)*1.2 = 360
        assertEquals(308, saved.getMemoryLimit());  // 256*1.2 = 307.2 -> 308
        assertEquals(120, saved.getVisibleCpuTimeLimit()); // 100*1.2 = 120
        assertEquals(154, saved.getVisibleMemoryLimit());  // 128*1.2 = 153.6 -> 154
    }

    @Test
    void getAllByProblemId_Success() {
        UUID problemId = UUID.randomUUID();
        List<Testcase> testcases = List.of(
                new Testcase(UUID.randomUUID(), "input1", "output1", true, 100, 128),
                new Testcase(UUID.randomUUID(), "input2", "output2", false, 200, 256)
        );

        ProblemTestcases problemTestcases = new ProblemTestcases();
        problemTestcases.setTestcases(testcases);

        when(repository.findById(problemId)).thenReturn(problemTestcases);

        List<Testcase> result = service.getAllByProblemId(problemId);

        assertEquals(2, result.size());
        assertEquals("input1", result.get(0).getInputData());
        assertEquals(100, result.get(0).getCpuTimeLimit()); // Verify new field
    }

}