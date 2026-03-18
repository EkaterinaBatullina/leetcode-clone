package com.technokratos.submissionserviceimpl.util;

import com.technokratos.submissionserviceimpl.entity.Testcase;
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

        ResourceLimitCalculator.Limits limits = ResourceLimitCalculator.compute(testcases);
        
        assertEquals(360, limits.cpuTimeLimit()); // (100+200)*1.2 = 360
        assertEquals(307, limits.memoryLimit());  // 256*1.2 = 307.2 -> 307
    }
}