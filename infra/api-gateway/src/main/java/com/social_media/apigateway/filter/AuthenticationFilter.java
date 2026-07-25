package com.social_media.apigateway.filter;

import com.social_media.apigateway.utils.GatewayConstants;
import com.social_media.apigateway.security.JwtValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtValidator jwtValidator;
    private final ReactiveStringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        //  Endpoint công khai
        if (path.contains("/auth/login") || path.contains("/users/register") || path.contains("/auth/logout") || path.contains("/auth/refresh-token")) {
            return chain.filter(exchange);
        }

        //  Lấy Authorization Header
        String authHeader = exchange.getRequest().getHeaders().getFirst(GatewayConstants.HEADER_AUTHOR);

        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer")) {
            log.warn("Missing or invalid Authorization header format for path: {}", path);
            return unauthenticated(exchange);
        }

        String token = authHeader.replaceFirst("(?i)Bearer", "").trim();

        //Kiểm tra token có nằm trong Sổ Bìa Đen (Redis) không?
        return redisTemplate.hasKey("blacklist:" + token)
                .flatMap(isBlacklisted -> {
                    // NẾU CÓ TRONG SỔ ĐEN -> ĐUỔI NGAY LẬP TỨC
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        log.warn("Lệnh cấm! Token đang nằm trong Blacklist: {}", path);
                        return unauthenticated(exchange);
                    }

                    //  Nếu không bị cấm, soi chữ ký và hạn sử dụng bằng Toán học như bình thường
                    try {
                        if (!jwtValidator.verifyToken(token)) {
                            return unauthenticated(exchange);
                        }
                        // Giải mã JWT để lấy ra ID của User (trong thuật ngữ JWT gọi là subject)
                        String subject = jwtValidator.extractSubject(token);
                        if (subject != null) {
                            // Tiến hành Mutation (Tạo bản sao) HTTP Request để Inject (Tiêm) Header định danh vào luồng nội bộ."
                            ServerWebExchange mutatedExchange = exchange.mutate()
                                    .request(exchange.getRequest().mutate()
                                            // Gắn Header "X-Auth-User-Id" = <subject> (tức là UserID)
                                            .header(com.social_media.common.utils.SecurityConstants.HEADER_USER_ID, subject)
                                            .build())
                                    .build();
                            return chain.filter(mutatedExchange);
                        }
                    } catch (Exception e) {
                        return unauthenticated(exchange);
                    }

                    //Cho phép Request (đã được chế biến) đi tiếp xuống Microservices bên dưới
                    return unauthenticated(exchange);
                });
    }

    private Mono<Void> unauthenticated(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        String body = "{\"code\": 1012, \"message\": \"Unauthenticated\", \"status\": 401}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return GatewayConstants.ORDER_JWT_AUTH_FILTER;
    }
}
