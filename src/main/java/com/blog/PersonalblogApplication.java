package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PersonalblogApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalblogApplication.class, args);
    }

}
