package com.tobe.healthy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HealthyApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthyApplication.class, args);
    }
}