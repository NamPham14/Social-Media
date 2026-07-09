package com.social_media.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import com.social_media.apigateway.utils.GatewayConstants;

@Component
public class SecurityHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Đăng ký hành động sẽ chạy "trước khi commit response"
        exchange.getResponse().beforeCommit(() -> {
            exchange.getResponse().getHeaders().add("X-Content-Type-Options", "nosniff");
            exchange.getResponse().getHeaders().add("X-Frame-Options", "DENY");
            exchange.getResponse().getHeaders().add("X-XSS-Protection", "1; mode=block");
            exchange.getResponse().getHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            exchange.getResponse().getHeaders().add("Content-Security-Policy", "default-src 'self'");
            exchange.getResponse().getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");

            return Mono.empty();
        });

        // Tiếp tục cho request đi qua các filter khác và xuống service
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return GatewayConstants.ORDER_SECURITY_HEADER_FILTER;
    }
}
