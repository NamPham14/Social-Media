package com.social_media.apigateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.apigateway.domain.RateLimitConfig;
import com.social_media.apigateway.utils.GatewayConstants;
import com.social_media.apigateway.utils.RateLimitUtils;
import com.social_media.common.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;


@Component
@Slf4j
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;


    public RateLimitFilter(ReactiveStringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String ip = "unknown";
        if (exchange.getRequest().getRemoteAddress() != null && exchange.getRequest().getRemoteAddress().getAddress() != null) {
            ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }

        // => dùng cho nginx các cấu hình to hơn.
//        List<String> xri = exchange.getRequest().getHeaders().get("X-Real-IP");
//        if (xri != null && !xri.isEmpty()) {
//            ip = xri.get(0);
//        } else {
//            List<String> xff = exchange.getRequest().getHeaders().get("X-Forwarded-For");
//            if (xff != null && !xff.isEmpty()) {
//                // Lấy IP cuối cùng nếu qua nhiều proxy tin tưởng, hoặc fix cứng theo hạ tầng của bạn
//                ip = xff.get(0).split(",")[0].trim();
//            } else if (exchange.getRequest().getRemoteAddress() != null) {
//                ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
//            }
//        }

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");

        RateLimitConfig config = RateLimitUtils.resolveConfig(path, method);

        long limit = config.getLimit();
        long windowSeconds = config.getWindowSeconds();



        String finalIp = ip;

        String identifier = (userId != null && !userId.isEmpty()) ? "user:" + userId : "ip:" + ip;
        String redisKey = String.format("rl_limit:%s:%s:%s", identifier, method, path);

        // Mono<Long> là trả về promis sẽ tăng cái rediskey đó
        // còn opsForValue => là việc với kiểu dữ liệu String
        //        String    opsForValue()
        //        Hash  opsForHash()
        //        List  opsForList()
        //        Set   opsForSet()
        Mono<Long> countMono = redisTemplate.opsForValue().increment(redisKey);

        return countMono
                .flatMap(currentCount -> {

                    //Khi gọi lệnh increment(), Redis không trả về con số 1, 2, 3 trần trụi, mà nó trả về một cái hộp kín có nhãn là Mono<Long>.
                    //không thể lấy cái hộp đó đi so sánh if (hộp > 10) được tại vì nó là promise
                    //Nhiệm vụ của flatMap chỉ duy nhất là: Đập vỡ cái hộp đó ra, lấy con số thực sự ở bên trong (hứng vào biến currentCount) để đem đi dùng.
                    // Luồng A: Nếu là request đầu tiên (bằng 1) -> Cho Redis chạy ngầm lệnh hẹn giờ 60 giây
                    if (currentCount == 1) {
                        return redisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds))
                                .then(Mono.just(currentCount));
                    }


                    //TTL: Time to live
                    //Gọi hàm getExpire để xem cái key redisKey có thời gian đc cài là bao nhiêu
                    //Nếu key đó k có hẹn h thì cái hàm trên trả về -1 => phải set lại cho nó
                    return redisTemplate.getExpire(redisKey)
                            .flatMap(ttl -> {
                                if (ttl.getSeconds() == -1) {
                                    return redisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds))
                                            .then(Mono.just(currentCount));
                                }
                                return Mono.just(currentCount);
                            });
                })
                .flatMap(currentCount -> {
                    // Luồng B: Nếu bấm quá 10 lần -> Khóa cửa chặn lại
                    if (currentCount > limit) {
                        log.warn("!!!!!!!! [RATE LIMIT] : IP {} đã bấm quá giới hạn ({}/{})", finalIp, currentCount, limit);

                        return Mono.error(new ResponseStatusException(
                                HttpStatus.TOO_MANY_REQUESTS,
                                String.format("Bạn đang thao tác quá nhanh! Vui lòng thử lại sau %d giây.", windowSeconds)
                        ));
                    }


                    // Luồng C: Dưới ngưỡng 10 -> Cho qua cửa vào trong
                    return chain.filter(exchange);
                })
                .onErrorResume(throwable -> {
                    log.error("!!!!!!!!! [REDIS ERROR] Redis bị sập hoặc lỗi kết nối! Thả xích cho request đi tiếp để tránh sập hệ thống.", throwable);
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return GatewayConstants.ORDER_RATE_LIMIT_FILTER;
    }
}
