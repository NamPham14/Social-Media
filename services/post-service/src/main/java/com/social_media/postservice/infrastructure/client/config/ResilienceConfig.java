package com.social_media.postservice.infrastructure.client.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.Retry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Configuration
public class ResilienceConfig {


    // [Feign]: Lỗi I/O mạng vật lý thô (Đứt cáp, sập server đột ngột...) được Feign bọc lại
    //feign.RetryableException.class,
    // [Java]: Lỗi xử lý bất đồng bộ hoặc kết nối chờ đợi bị quá hạn chung của Java
    //java.util.concurrent.TimeoutException.class,
    // [Java Net]: Lỗi thiết bị mạng báo hết giờ chờ gói tin phản hồi (Read/Connect Timeout)
    //java.net.SocketTimeoutException.class,
    // [Java Net]: Lỗi sập server đích, IP đúng nhưng tiến trình tắt, không mở Port (Connection refused)
    //java.net.ConnectException.class,
    // [Gateway 504]: Lỗi API Gateway phản hồi về do Identity Service xử lý quá lâu
    //feign.FeignException.GatewayTimeout.class,
    // [Gateway 502]: Lỗi API Gateway không kết nối được tới node mạng hợp lệ của Identity
    //feign.FeignException.BadGateway.class,
    // [Gateway 503]: Lỗi Eureka/Gateway báo hiện tại không có máy chủ Identity nào đang bật
    //feign.FeignException.ServiceUnavailable.class
    @Bean
    public Retry identityRetry(RetryRegistry retryRegistry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3) // Thử lại tối đa 3 lần
                .waitDuration(Duration.ofMillis(1000)) // Thời gian chờ giữa các lần thử lại là 1 giây
                .retryExceptions(
                        feign.RetryableException.class,   // Lỗi kết nối ngoại vi từ Feign
                        TimeoutException.class,          // Lỗi quá thời gian chờ (Read/Connect Timeout)
                        feign.FeignException.GatewayTimeout.class,
                        feign.FeignException.ServiceUnavailable.class// Lỗi 504 nếu đi qua Gateway bị chậm
                )
                .ignoreExceptions(
                        feign.FeignException.BadRequest.class, // Bỏ qua lỗi 400
                        feign.FeignException.Unauthorized.class, // Bỏ qua lỗi 401
                        feign.FeignException.Forbidden.class // Bỏ qua lỗi 403
                )
                .build();
        return retryRegistry.retry("identityRetry", config);
    }


    // Chọn loại thống kê dựa trên SỐ LƯỢNG REQUEST (Thay vì dựa trên khoảng thời gian)
    //.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
    // Kích thước cửa sổ trượt: Chỉ quan sát và tính toán số liệu của 10 request gần nhất
    //.slidingWindowSize(10)
    // Ngưỡng tỷ lệ lỗi (50%): Trong 10 request, nếu có từ 5 request trở lên bị sập/lỗi ──► CẦU CHÌ SẼ NGẮT (Mạch OPEN)
    //.failureRateThreshold(50)
    // Ngưỡng tỷ lệ xử lý chậm (75%): Trong 10 request, nếu có từ 8 request trở lên bị rùa bò ──► CẦU CHÌ CŨNG NGẮT
    //.slowCallRateThreshold(75)
    // Định nghĩa thế nào là "Chậm": Request nào tốn QUÁ 3 GIÂY để phản hồi thì bị tính là 1 lần xử lý chậm
    //.slowCallDurationThreshold(Duration.ofSeconds(3))
    // Thời gian mạch nằm im ở trạng thái OPEN: Sau khi ngắt mạch 10 giây (Để cho Identity hồi sức cấp cứu),
    // mạch sẽ tự chuyển sang HALF-OPEN (Mở hé) để cho vài request đi thử nghiệm lại xem hệ thống đã ổn định chưa.
    @Bean
    public CircuitBreaker identityCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10) // Thống kê dựa trên 10 request gần nhất
                .failureRateThreshold(50) // Nếu >= 50% request bị lỗi (5/10), ngắt mạch ngay (OPEN)
                .slowCallRateThreshold(75) // Nếu >= 75% request bị chậm, cũng ngắt mạch
                .slowCallDurationThreshold(Duration.ofSeconds(3)) // Request xử lý quá 3 giây coi là chậm
                .waitDurationInOpenState(Duration.ofSeconds(10)) // Giữ mạch OPEN trong 10 giây rồi chuyển HALF-OPEN
                .build();
        return circuitBreakerRegistry.circuitBreaker("identityCircuitBreaker", config);
    }


    @Bean
    public Retry profileRetry(RetryRegistry retryRegistry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3) // Thử lại tối đa 3 lần
                .waitDuration(Duration.ofMillis(1000)) // Chờ 1 giây giữa các lần thử
                .retryExceptions(
                        feign.RetryableException.class,   // Lỗi kết nối ngoại vi từ Feign
                        TimeoutException.class,          // Lỗi quá thời gian chờ (Read/Connect Timeout)
                        feign.FeignException.GatewayTimeout.class, // 504
                        feign.FeignException.ServiceUnavailable.class // 503
                )
                .ignoreExceptions(
                        feign.FeignException.BadRequest.class,    // 400 - không nên retry
                        feign.FeignException.Unauthorized.class,  // 401
                        feign.FeignException.Forbidden.class,     // 403
                        feign.FeignException.NotFound.class       // 404 - user không có profile, retry vô ích
                )
                .build();
        return retryRegistry.retry("profileRetry", config);
    }

    @Bean
    public CircuitBreaker profileCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10) // Thống kê dựa trên 10 request gần nhất
                .failureRateThreshold(50) // >= 50% lỗi thì ngắt mạch (OPEN)
                .slowCallRateThreshold(75) // >= 75% request chậm cũng ngắt mạch
                .slowCallDurationThreshold(Duration.ofSeconds(3)) // Quá 3s coi là chậm
                .waitDurationInOpenState(Duration.ofSeconds(10)) // Giữ mạch OPEN 10s rồi thử lại HALF-OPEN
                .build();
        return circuitBreakerRegistry.circuitBreaker("profileCircuitBreaker", config);
    }

    // hiếu thêm
    @Bean
    public Retry followerRetry(RetryRegistry retryRegistry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1000))
                .retryExceptions(
                        feign.RetryableException.class,
                        TimeoutException.class,
                        feign.FeignException.GatewayTimeout.class,
                        feign.FeignException.ServiceUnavailable.class
                )
                .ignoreExceptions(
                        feign.FeignException.BadRequest.class,
                        feign.FeignException.Unauthorized.class,
                        feign.FeignException.Forbidden.class,
                        feign.FeignException.NotFound.class
                )
                .build();
        return retryRegistry.retry("followerRetry", config);
    }

    // hiếu thêm
    @Bean
    public CircuitBreaker followerCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .failureRateThreshold(50)
                .slowCallRateThreshold(75)
                .slowCallDurationThreshold(Duration.ofSeconds(3))
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build();
        return circuitBreakerRegistry.circuitBreaker("followerCircuitBreaker", config);
    }

    @Bean
    public Retry interactionRetry(RetryRegistry retryRegistry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1000))
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
        return retryRegistry.retry("interactionRetry", config);
    }

    @Bean
    public CircuitBreaker interactionCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .failureRateThreshold(50)
                .slowCallRateThreshold(75)
                .slowCallDurationThreshold(Duration.ofSeconds(3))
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build();
        return circuitBreakerRegistry.circuitBreaker("interactionCircuitBreaker", config);
    }

    @Bean
    public Retry commentRetry(RetryRegistry retryRegistry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1000))
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
        return retryRegistry.retry("commentRetry", config);
    }

    @Bean
    public CircuitBreaker commentCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .failureRateThreshold(50)
                .slowCallRateThreshold(75)
                .slowCallDurationThreshold(Duration.ofSeconds(3))
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build();
        return circuitBreakerRegistry.circuitBreaker("commentCircuitBreaker", config);
    }

    @Bean
    public Retry geminiRetry(RetryRegistry retryRegistry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(3)) // 429 thường chỉ cần đợi vài giây
                .retryExceptions(
                        ResourceAccessException.class,             // Timeout / Rớt mạng
                        HttpServerErrorException.class,           // Lỗi 5xx
                        HttpClientErrorException.TooManyRequests.class // CẦN THÊM: Bắt lỗi 429
                )
                .ignoreExceptions(
                        IllegalStateException.class               // Lỗi parse JSON
                )
                .build();
        return retryRegistry.retry("geminiRetry", config);
    }

    @Bean
    public CircuitBreaker geminiCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10) // Thống kê dựa trên 10 request gần nhất
                .failureRateThreshold(50) // >= 50% lỗi thì ngắt mạch (OPEN)
                .slowCallRateThreshold(75) // >= 75% request chậm cũng ngắt mạch
                .slowCallDurationThreshold(Duration.ofSeconds(3)) // Quá 3s coi là chậm
                .waitDurationInOpenState(Duration.ofSeconds(30)) // Giữ mạch OPEN 30s rồi thử lại HALF-OPEN
                .build();
        return circuitBreakerRegistry.circuitBreaker("geminiCircuitBreaker", config);
    }
}