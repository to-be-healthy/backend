package com.tobe.healthy;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

import static java.util.TimeZone.getTimeZone;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
@EnableAsync
public class HealthyApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthyApplication.class, args);
    }

    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(getTimeZone("Asia/Seoul"));
    }
}