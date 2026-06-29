package com.social_media.postservice.infrastructure.client.identity.service;

import com.social_media.common.exception.BusinessRuleViolationException;
import com.social_media.common.exception.EntityNotFoundException;
import com.social_media.common.exception.ServiceUnavailableException;
import com.social_media.postservice.infrastructure.client.identity.IdentityClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdentityServiceHelper {

    private final IdentityClient identityClient;

    @Retry(name = "identityRetry")
    @CircuitBreaker(name = "identityCircuitBreaker", fallbackMethod = "fallbackUserStatus")
//    @CircuitBreaker(name = "identityCircuitBreaker")
    public String getSafeUserStatus(UUID userId) {
        log.info("Đang kết nối sang Identity Service kiểm tra trạng thái User: {}", userId);
        return identityClient.getUserStatus(userId);
    }

    public String fallbackUserStatus(UUID userId, Throwable throwable) {
        log.error("Fallback Active: Kích hoạt bảo vệ cho User: {}", userId);
        log.error("-> Nguyên nhân gốc gây lỗi (Throwable): {}", throwable.getClass().getName());

        if (throwable instanceof BusinessRuleViolationException || throwable instanceof EntityNotFoundException) {
            log.info("-> [Xử lý]: Phát hiện lỗi nghiệp vụ từ Decoder.!");
            throw (RuntimeException) throwable;
        }

        log.warn("-> [Xử lý]: Phát hiện Identity-Service bị sập nguồn hoặc nghẽn mạng.");

        throw new ServiceUnavailableException("Không thể kết nối đến Identity Service. Vui lòng thử lại sau.");
    }
}