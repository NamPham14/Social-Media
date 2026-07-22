package com.social_media.chatservice.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.social_media.chatservice.infrastructure.client")
public class FeignConfig {}
