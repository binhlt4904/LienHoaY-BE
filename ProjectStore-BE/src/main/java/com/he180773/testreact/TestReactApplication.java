package com.he180773.testreact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.he180773.testreact.repository")
@EnableScheduling
public class TestReactApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestReactApplication.class, args);
    }
}