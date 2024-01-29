package com.tobe.healthy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// Spring Security 임시 끄기

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class HealthyApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthyApplication.class, args);
    }

}