package com.technokratos.integration.base;

import com.technokratos.config.TestContainersConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
public abstract class BaseIntegrationTest {
    @MockBean
    private JavaMailSender mailSender;
}
