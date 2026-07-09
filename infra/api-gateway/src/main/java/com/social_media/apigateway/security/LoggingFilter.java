package com.social_media.apigateway.security;

import com.social_media.apigateway.utils.GatewayConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. lấy các request và sàng lọc
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        // 1.2. Lọc bypass các request hệ thống
        if (path.startsWith("/actuator") || path.endsWith("favicon.ico") || path.contains("/v3/api-docs")) {
            return chain.filter(exchange); // Đi tiếp luôn, không thực hiện các bước log phía dưới // giống với CorrelationFilter
        }

        // 2. khởi tạo thời gian ghi log
        // 2.1 bấm thời gian start để tý trừ đi tính thời gian execute 1 cái gì đó
        long startTime = System.currentTimeMillis();

        // 2.2. in log lúc request mới vào hệ thống (MDC đã có ID từ CorrelationFilter)
        log.info("===> [REQUEST]  : [{}] {}", method, path);

        // 3. đón đầu response đăng ký fallback responese
        return chain.filter(exchange)
                // 3.1. nếu success có phản hồi trả về từ Backend (Thành công hoặc Lỗi nghiệp vụ 400, 500)
                .doOnSuccess(aVoid -> {
                    ServerHttpResponse response = exchange.getResponse();
                    long executeTime = System.currentTimeMillis() - startTime; // tính thời gian xử lý

                    log.info("<=== [RESPONSE] : [{}] {} | Status: {} | Time: {}ms",
                            method, path, response.getStatusCode(), executeTime);
                })
                // 3.2. nếu error sập kết nối mạng, sập nguồn hoặc timeout giữa chừng
                .doOnError(throwable -> {
                    long executeTime = System.currentTimeMillis() - startTime; // Tính thời gian xử lý

                    log.error("!!!! [ERROR]    : [{}] {} | Reason: {} | Time: {}ms",
                            method, path, throwable.getMessage(), executeTime);
                });
    }

    @Override
    public int getOrder() {
        return GatewayConstants.ORDER_LOGGING_FILTER;
    }

}
