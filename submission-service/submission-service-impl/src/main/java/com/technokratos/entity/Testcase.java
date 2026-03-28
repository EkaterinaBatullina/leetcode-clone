package com.technokratos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Testcase {
    @Id
    private UUID id;
    private String inputData;
    private String expectedOutput;
    private boolean visible;
    private Integer cpuTimeLimit;
    private Integer memoryLimit;
}
