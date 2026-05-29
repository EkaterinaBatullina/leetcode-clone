package com.technokratos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ProblemServiceImplApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProblemServiceImplApplication.class, args);
    }

}
