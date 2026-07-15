package com.social_media.postservice.infrastructure.client.config; // Sửa lỗi chính tả cilent -> client nhé bạn

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            log.warn("FeignInterceptor: No RequestContext found. Token cannot be propagated to {}", requestTemplate.url());
            return;
        }

        String authHeader = attributes.getRequest().getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_TOKEN_PREFIX)) {
            requestTemplate.header(AUTHORIZATION_HEADER, authHeader);

//            if (log.isDebugEnabled()) {
//                log.debug("FeignInterceptor: Successfully propagated token (Prefix: {}...) to {}",
//                        authHeader.substring(0, Math.min(authHeader.length(), 15)), requestTemplate.url());
//            }
            log.info("FeignInterceptor: Propagating JWT: {}", authHeader);
        } else {
            log.warn("FeignInterceptor: Authorization header is missing or invalid for request to {}", requestTemplate.url());
        }
    }
}
