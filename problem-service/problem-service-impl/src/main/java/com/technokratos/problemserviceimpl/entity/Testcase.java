package com.technokratos.problemserviceimpl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "testcase")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Testcase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "input_data", columnDefinition = "TEXT")
    private String inputData;

    @Column(name = "expected_output", columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(name = "cpu_time_limit")
    private Integer cpuTimeLimit;

    @Column(name = "memory_limit")
    private Integer memoryLimit;

    @Column(name = "visible")
    private Boolean visible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;
}
