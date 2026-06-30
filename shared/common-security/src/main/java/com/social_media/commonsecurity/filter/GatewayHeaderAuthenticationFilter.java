package com.social_media.commonsecurity.filter;

import com.social_media.common.utils.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter bảo mật dùng chung cho tất cả các Microservices (trừ API Gateway và Identity Service).
 * <p>
 * KIẾN TRÚC BẢO MẬT (Offload Authentication to Gateway):
 * - Các Service con không cần phải tự giải mã JWT Token nữa (rất tốn tài nguyên và lặp code).
 * - Nhiệm vụ giải mã JWT được giao phó hoàn toàn cho API Gateway.
 * - Sau khi Gateway giải mã xong, nó sẽ truyền `userId` xuống các Service con thông qua HTTP Header (X-Auth-User-Id).
 * - Filter này có nhiệm vụ "hứng" cái Header đó và nhét vào Spring Security Context.
 * </p>
 * 
 * Lợi ích: Tối ưu hiệu năng, code sạch sẽ, kiến trúc chuẩn Enterprise.
 */
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // láy userId từ header mà Api Gateway ném xuống
        String userId = request.getHeader(SecurityConstants.HEADER_USER_ID);

        //Nếu có userId thì cấp quyền (Set Security Context)
        if (userId != null && !userId.isEmpty()) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);

    }
}
