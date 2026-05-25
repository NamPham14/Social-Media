package com.social_media.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@SpringBootApplication
@EnableJpaAuditing
public class UsersServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersServiceApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () ->
//                Optional.ofNullable(
//                        SecurityContextHolder.getContext().getAuthentication())
//                .map(Authentication::getName)
//                .or(() ->
                Optional.of("system");
    }
}
