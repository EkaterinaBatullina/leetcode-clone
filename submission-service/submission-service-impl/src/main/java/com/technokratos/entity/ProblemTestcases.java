package com.technokratos.entity;

import com.technokratos.problemserviceapi.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "problem_testcases")
public class ProblemTestcases {
    @Id
    private UUID id;
    private Difficulty difficulty;
    private String visibleInputs;
    private String visibleOutputs;
    private int cpuTimeLimit;
    private int memoryLimit;
    private Integer visibleCpuTimeLimit;
    private Integer visibleMemoryLimit;
    private String inputs;
    private String outputs;
    private List<Testcase> testcases;
}