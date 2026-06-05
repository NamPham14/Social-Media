package com.social_media.followerservice.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.social_media.followerservice.infrastructure.client")
public class FeignConfig {
}
