package com.technokratos;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class NotificationServiceApplication {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}