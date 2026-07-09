package com.social_media.apigateway.utils;

import com.social_media.apigateway.domain.RateLimitConfig;

import java.util.Map;

public class RateLimitUtils {

    // định nghĩa sẵn cấu hình mặc định (Default) nếu API không khớp cái nào
    private static final RateLimitConfig DEFAULT_CONFIG = new RateLimitConfig(10, 60);

    // tạo một cái Registry tra cứu bằng Map.
    private static final Map<String, RateLimitConfig> LIMIT_REGISTRY = Map.of(
            "POST:/post/api/v1/posts", new RateLimitConfig(5, 60),
            "POST:/post/api/v1/posts/search", new RateLimitConfig(5, 30)
    );


    //tìm kiếm cấu hình Rate Limit phù hợp cho API đang gọi
    public static RateLimitConfig resolveConfig(String path, String method) {
        String apiKey = method.toUpperCase() + ":" + path;

        RateLimitConfig config = LIMIT_REGISTRY.get(apiKey);

        return (config != null) ? config : DEFAULT_CONFIG;
    }
}
