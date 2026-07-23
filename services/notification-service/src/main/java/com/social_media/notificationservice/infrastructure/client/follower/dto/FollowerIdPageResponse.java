package com.social_media.notificationservice.infrastructure.client.follower.dto;

import java.util.List;

public record FollowerIdPageResponse(
        List<String> items,
        int currentPage,
        int pageSize,
        int totalPages,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious
) {
}
