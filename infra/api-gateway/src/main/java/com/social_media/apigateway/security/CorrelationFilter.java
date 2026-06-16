package com.social_media.apigateway.security;

import com.social_media.apigateway.utils.GatewayConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;


@Component
@Slf4j
public class CorrelationFilter implements GlobalFilter, Ordered {

    private static final String MDC_KEY = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // lọc bypass các request hệ thống để tránh rác log
        if (path.startsWith("/actuator") || path.endsWith("favicon.ico") || path.contains("/v3/api-docs")) {
            return chain.filter(exchange);
        }

        // 1. kiểm tra xem header có X-Correlation-ID hay chưa
        String correlationId = request.getHeaders().getFirst(GatewayConstants.CORRELATION_ID_HEADER);

        // nếu không có => tạo mới
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        }

        // đảm bảo biến final này luôn có giá trị chính xác sau khi đã check/tạo mới
        final String finalCorrelationId = correlationId;

        // 2. set correlationId vào request header để gửi sang các microservice con phía sau
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(GatewayConstants.CORRELATION_ID_HEADER, finalCorrelationId)
                .build();

        // 3. đăng ký callback bổ sung header vào response trước khi kết nối bị đóng (chắc chắn client nhận được)
        //Hàm beforeCommit() trong Spring WebFlux (và Spring Cloud Gateway) được sử dụng để đăng ký một hành động (callback)
        // sẽ tự động kích hoạt ngay trước khi các gói tin dữ liệu (HTTP Response Headers & Body) thực sự được ghi nhận và gửi về cho Client.
        exchange.getResponse().beforeCommit(() -> {
            exchange.getResponse().getHeaders().set(GatewayConstants.CORRELATION_ID_HEADER, finalCorrelationId);
            return Mono.empty();
        });

        //4. ném nó vào MDC để sau còn loging
        // MDC là một ThreadLocal Map.
        // Mỗi thread sẽ có một Map<String, String> riêng,
        // dùng để lưu các thông tin ngữ cảnh (context)
        // như correlationId, requestId, userId...
        //MDC.put(MDC_KEY, finalCorrelationId);

        // 5. tiến hành forward request đi tiếp
        // truyền exchain vào filter là vì exchain bao bọc tất cả:
        // exchange
        // ├── request
        // ├── response
        // ├── attributes
        // └── session...
        // exchange.mutate().request(request).build() => tạo 1 biến exchain mới truyền cái request vào với tham số là request rồi build
        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                // Thay vì .then(), dùng .doFinally() để đảm bảo dù request thành công hay lỗi
                // Then() => lỗi là dừng
                // thì Thread này cũng được dọn dẹp MDC sạch sẽ, tránh rò rỉ bộ nhớ.

                // giải thích cho cái beforeCommit
                // -> NGAY TẠI ĐÂY: Khi chain.filter xử lý xong và chuẩn bị nhả data về cho client,
                // Sự kiện "Commit" kích hoạt -> Đoạn code setHeader ở trên lập tức được lôi ra chạy -> Header được add thành công -> Gửi về cho Client!
                .contextWrite(Context.of(MDC_KEY, correlationId));
    }

    @Override
    public int getOrder() {
        return GatewayConstants.ORDER_CORRELATION_FILTER;
    }


}
