package com.social_media.followerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.social_media")
public class FollowerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FollowerServiceApplication.class, args);
    }

}
