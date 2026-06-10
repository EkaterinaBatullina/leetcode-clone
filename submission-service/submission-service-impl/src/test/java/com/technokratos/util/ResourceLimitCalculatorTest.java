package com.technokratos.util;

import com.technokratos.entity.Testcase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceLimitCalculatorTest {

    @Test
    void computeLimits_Success() {
        List<Testcase> testcases = List.of(
                new Testcase(null, null, null, true, 100, 128),
                new Testcase(null, null, null, false, 200, 256)
        );

        ResourceLimitCalculator.Limits limits =
                ResourceLimitCalculator.compute(testcases);

        assertEquals(360, limits.cpuTimeLimit());
        assertEquals(308, limits.memoryLimit());
    }
}