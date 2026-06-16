//package com.social_media.postservice.config;
//
//
//import com.social_media.postservice.utils.Constants;
//import com.social_media.postservice.utils.UserUtils;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.InsufficientAuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//@Slf4j
//public class PostServiceFilter extends OncePerRequestFilter {
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String correlationId = request.getHeader(Constants.CORRELATION_ID_HEADER);
//        String userId = request.getHeader(Constants.HEADER_USER_ID);
//        String username = request.getHeader(Constants.HEADER_USER_NAME);
//        String userRoles = request.getHeader(Constants.HEADER_USER_ROLES);
//
//        log.info("Filter - Correlation ID {}", correlationId);
//        log.info("Filter - User ID {}", userId);
//        log.info("Filter - User name {}", username);
//        log.info("Filter - Roles {}", userRoles);
//
//        if (userId == null || username == null || userRoles == null) {
//            log.error("Filter - Missing required headers from Gateway");
//            throw new InsufficientAuthenticationException("Missing user identity headers");
//        }
//
//        List<GrantedAuthority> grantedAuthorities = UserUtils.extractRole(userRoles);
//
//
//    }
//}
