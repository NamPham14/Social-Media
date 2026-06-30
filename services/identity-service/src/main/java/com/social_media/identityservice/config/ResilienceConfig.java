package com.social_media.identityservice.config;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Configuration
public class ResilienceConfig {

    @Bean
    public Retry profileRetry(RetryRegistry retryRegistry) {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(
                        feign.RetryableException.class,
                        TimeoutException.class,
                        feign.FeignException.GatewayTimeout.class,
                        feign.FeignException.ServiceUnavailable.class
                )
                .ignoreExceptions(
                        feign.FeignException.BadRequest.class,
                        feign.FeignException.Unauthorized.class,
                        feign.FeignException.Forbidden.class
                )
                .build();
        return retryRegistry.retry("profileRetry", retryConfig);
    }


    @Bean
    public CircuitBreaker  profileCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .failureRateThreshold(50)
                .slowCallRateThreshold(50)
                .slowCallDurationThreshold(Duration.ofSeconds(3))
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build();
        return circuitBreakerRegistry.circuitBreaker("profileCircuitBreaker", config);
    }



}
