package com.social_media.postservice.infrastructure.client.config;

import com.social_media.postservice.infrastructure.client.decoder.CustomErrorDecoder;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;


public class FeignClientConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)

                .build();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    public feign.RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String userId = request.getHeader("X-Auth-User-Id");
                if (userId != null) {
                    requestTemplate.header("X-Auth-User-Id", userId);
                }
            }
        };
    }
}
