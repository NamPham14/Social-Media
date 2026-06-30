package com.social_media.identityservice.infrastructure.client;


import com.social_media.common.api.ApiResponse;
import com.social_media.common.exception.BusinessRuleViolationException;
import com.social_media.identityservice.api.dto.request.ProfileCreationRequest;
import com.social_media.identityservice.infrastructure.exception.ProfileServiceDownException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceHelper {
    private final ProfileClient profileClient;

    @Retry(name="profileRetry")
    @CircuitBreaker(name = "profileCircuitBreaker", fallbackMethod = "fallbackCreateProfile")
    public ApiResponse<Object> createSafeProfile(ProfileCreationRequest request){
        log.info("Đang kết nối sang Profile Service để tạo profile cho user...");
        return profileClient.createProfile(request);
    }
    // Hàm Fallback phải có Signature tham số y hệt hàm gốc, cộng thêm tham số Throwable ở cuối cùng
    public ApiResponse<Object> fallbackCreateProfile(ProfileCreationRequest request, Throwable throwable){
        log.error("Fallback Active: Kích hoạt bảo vệ ngắt mạch khi gọi Profile Service!");
        log.error("-> Nguyên nhân gốc gây lỗi (Throwable): {}", throwable.getClass().getName());

        if (throwable instanceof BusinessRuleViolationException) {
            log.info("-> [Xử lý]: Phát hiện lỗi nghiệp vụ hợp lệ, ném ngoại lệ lên trên.");
            throw (RuntimeException) throwable;
        }

        log.warn("-> [Xử lý]: Phát hiện Profile-Service sập nguồn hoặc nghẽn mạng.");

        // Thất bại nhanh (Fail-Fast): Ném lỗi thân thiện để GlobalExceptionHandler xử lý
        throw new ProfileServiceDownException("Hệ thống hồ sơ đang bảo trì. Không thể tạo profile lúc này.");
    }


}
