package com.social_media.apigateway.filter;

import com.social_media.commonsecurity.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1. Endpoint công khai
        if (path.contains("/auth/login") || path.contains("/users/register")) {
            return chain.filter(exchange);
        }

        // 2. Lấy Authorization Header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer")) {
            log.warn("Missing or invalid Authorization header format for path: {}", path);
            return unauthenticated(exchange);
        }

        // CÁCH LẤY TOKEN MỚI: Cực kỳ an toàn, loại bỏ "Bearer" và khoảng trắng dư thừa
        String token = authHeader.replaceFirst("(?i)Bearer", "").trim();

        // 3. Kiểm tra token
        try {
            log.debug("Verifying token for path: {}", path);
            if (!jwtProvider.verifyToken(token)) {
                log.warn("Invalid token for path: {}. Token might be expired or signature is invalid.", path);
                return unauthenticated(exchange);
            }
        } catch (Exception e) {
            log.error("Token verification error for path {}: {}", path, e.getMessage(), e);
            return unauthenticated(exchange);
        }

        log.info("Token verified successfully for path: {}", path);
        return chain.filter(exchange);
    }

    private Mono<Void> unauthenticated(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        String body = "{\"code\": 1012, \"message\": \"Unauthenticated\", \"status\": 401}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
