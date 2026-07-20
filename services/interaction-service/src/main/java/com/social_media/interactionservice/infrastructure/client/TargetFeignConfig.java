package com.social_media.interactionservice.infrastructure.client;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

public class TargetFeignConfig {
    @Bean
    RequestInterceptor correlationInterceptor() {
        return template -> {
            String id = MDC.get("correlationId");
            if (id != null) template.header("X-Correlation-Id", id);
        };
    }
}
