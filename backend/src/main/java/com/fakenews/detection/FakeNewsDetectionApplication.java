package com.fakenews.detection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application entry point for Fake News Detection System.
 * College Full Stack Mini Project.
 */
@SpringBootApplication
public class FakeNewsDetectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(FakeNewsDetectionApplication.class, args);
        System.out.println("=================================================");
        System.out.println(" Fake News Detection System Backend is RUNNING! ");
        System.out.println(" Server URL: http://localhost:8080               ");
        System.out.println("=================================================");
    }
}
