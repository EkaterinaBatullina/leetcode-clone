package com.technokratos.problemserviceimpl.service;

import com.technokratos.problemserviceimpl.BaseServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CodeWrappingServiceTest extends BaseServiceTest {
    @Autowired
    private CodeWrappingService codeWrappingService;

    @Test
    void wrapUserCode() {
        String result = codeWrappingService.wrapUserCode(
                "System.out.println(1);",
                "public class Main { __USER_CODE__ }"
        );

        assertEquals("public class Main { System.out.println(1); }", result);
    }

    @Test
    void wrapUserCode_missingPlaceholder() {
        assertThrows(IllegalArgumentException.class, () ->
                codeWrappingService.wrapUserCode("code", "no placeholder")
        );
    }
}