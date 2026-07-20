package com.social_media.followerservice.config.security;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UUID getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        if (authentication.getPrincipal() instanceof CustomUserDetail customUserDetail) {
            return customUserDetail.getUserId();
        }
        throw new IllegalStateException("Principal is not CustomUserDetail");
    }
}
