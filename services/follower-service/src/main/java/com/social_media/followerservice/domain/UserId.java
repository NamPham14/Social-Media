package com.social_media.followerservice.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record UserId(Long value) {
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId value cannot be null");
        }
    }
}
