package com.technokratos.service.base;

import com.technokratos.problemserviceapi.dto.request.PublishTestcasesRequest;
import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.entity.Testcase;

import java.util.List;
import java.util.UUID;

public interface BaseProblemTestcasesService {
    List<Testcase> getAllByProblemId(UUID problemId);

    List<Testcase> getAllVisibleByProblemId(UUID problemId);

    Difficulty getProblemDifficulty(UUID problemId);

    String getInputs(UUID problemId);

    String getOutputs(UUID problemId);

    String getVisibleInputs(UUID problemId);

    String getVisibleOutputs(UUID problemId);

    Integer getCpuTimeLimit(UUID problemId);

    Integer getMemoryLimit(UUID problemId);

    Integer getVisibleCpuTimeLimit(UUID problemId);

    Integer getVisibleMemoryLimit(UUID problemId);

    UUID create(PublishTestcasesRequest publishTestcasesRequest);
}

