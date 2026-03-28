package com.technokratos.util;

import com.technokratos.entity.Testcase;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ResourceLimitCalculator {

    private static final double SAFETY_MULTIPLIER = 1.2;

    public static int computeTotalCpuTimeLimit(List<Testcase> testcases) {
        List<Integer> cpuTimes = testcases.stream()
                .map(Testcase::getCpuTimeLimit)
                .toList();
        log.debug("time limits of each: {}", cpuTimes);
        int total = cpuTimes.stream().mapToInt(Integer::intValue).sum();
        return (int) Math.ceil(total * SAFETY_MULTIPLIER);
    }

    public static int computeMaxMemoryLimit(List<Testcase> testcases) {
        List<Integer> memoryLimits = testcases.stream()
                .map(Testcase::getMemoryLimit)
                .toList();
        int max = memoryLimits.stream().mapToInt(Integer::intValue).max().orElse(128 * 1024);
        return (int) Math.ceil(max * SAFETY_MULTIPLIER);
    }

    public record Limits(int cpuTimeLimit, int memoryLimit) {}

    public static Limits compute(List<Testcase> testcases) {
        return new Limits(
                computeTotalCpuTimeLimit(testcases),
                computeMaxMemoryLimit(testcases)
        );
    }
}