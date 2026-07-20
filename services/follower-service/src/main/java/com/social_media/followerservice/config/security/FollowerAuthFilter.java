package com.social_media.followerservice.config.security;

import com.social_media.common.utils.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class FollowerAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String userIdStr = request.getHeader(SecurityConstants.HEADER_USER_ID);

        if (userIdStr != null) {
            try {
                UUID userId = UUID.fromString(userIdStr);
                CustomUserDetail userDetail = new CustomUserDetail(userId);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Set authentication for user: {}", userId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid X-Auth-User-Id header value: {}", userIdStr);
            }
        }

        filterChain.doFilter(request, response);
    }
}
