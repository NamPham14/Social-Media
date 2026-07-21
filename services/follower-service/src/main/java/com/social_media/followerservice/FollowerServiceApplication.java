package com.social_media.followerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.social_media")
@EnableScheduling
public class FollowerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FollowerServiceApplication.class, args);
    }

}
