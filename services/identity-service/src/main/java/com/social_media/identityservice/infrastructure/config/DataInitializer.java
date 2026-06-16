//package com.social_media.identityservice.infrastructure.config;
//
//import com.social_media.identityservice.domain.Role;
//import com.social_media.identityservice.domain.RoleRepository;
//import com.social_media.identityservice.domain.User;
//import com.social_media.identityservice.domain.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Set;
//
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class DataInitializer {
//
//    private final PasswordEncoder passwordEncoder;
//
//    @Bean
//    public ApplicationRunner initData(UserRepository userRepository, RoleRepository roleRepository) {
//        return args -> {
//            log.info("Initializing data...");
//
//            // 1. Khởi tạo Role ADMIN nếu chưa có
//            Role adminRole = roleRepository.findByName("ADMIN")
//                    .orElseGet(() -> {
//                        log.info("Creating ADMIN role...");
//                        return roleRepository.save(Role.builder()
//                                .name("ADMIN")
//                                .build());
//                    });
//
//            // 2. Khởi tạo Role USER nếu chưa có (để dùng sau này)
//            roleRepository.findByName("USER")
//                    .orElseGet(() -> {
//                        log.info("Creating USER role...");
//                        return roleRepository.save(Role.builder()
//                                .name("USER")
//                                .build());
//                    });
//
//            // 3. Khởi tạo tài khoản ADMIN mặc định
//            if (!userRepository.existsByUsername("admin")) {
//                log.info("Creating default admin user...");
//                User admin = User.builder()
//                        .username("admin")
//                        .password(passwordEncoder.encode("admin123"))
//                        .email("admin@socialmedia.com")
//                        .roles(Set.of(adminRole))
//                        .build();
//                userRepository.save(admin);
//                log.info("Default admin user created successfully.");
//            } else {
//                log.info("Admin user already exists.");
//            }
//        };
//    }
//}
