package com.tobe.healthy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
@EnableAsync
public class HealthyApplication {
    private static final Logger log = LoggerFactory.getLogger(HealthyApplication.class);

    public static void main(String[] args) {
        log.info("Application Start");
        SpringApplication.run(HealthyApplication.class, args);
    }
}