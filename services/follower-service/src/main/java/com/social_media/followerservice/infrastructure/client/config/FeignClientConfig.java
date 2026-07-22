package com.social_media.followerservice.infrastructure.client.config;

import com.social_media.followerservice.infrastructure.client.decoder.CustomErrorDecoder;
import com.social_media.common.utils.SecurityConstants;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String userId = request.getHeader(SecurityConstants.HEADER_USER_ID);
                if (userId != null) {
                    requestTemplate.header(SecurityConstants.HEADER_USER_ID, userId);
                }
            }
        };
    }
}
