package com.social_media.followerservice.domain.model;

public record UserId(Long value) {
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId value cannot be null");
        }
    }
}
