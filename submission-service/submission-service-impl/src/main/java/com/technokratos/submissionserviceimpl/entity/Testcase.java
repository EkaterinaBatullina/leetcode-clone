package com.technokratos.submissionserviceimpl.entity;

<<<<<<< HEAD
=======
import lombok.AllArgsConstructor;
>>>>>>> feature/problem-and-submission-service
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
<<<<<<< HEAD
=======
@AllArgsConstructor
>>>>>>> feature/problem-and-submission-service
public class Testcase {
    @Id
    private UUID id;
    private String inputData;
    private String expectedOutput;
    private boolean visible;
    private Integer cpuTimeLimit;
    private Integer memoryLimit;
}
