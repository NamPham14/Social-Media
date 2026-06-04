package com.social_media.followerservice.domain.model;

import com.social_media.followerservice.domain.exception.BusinessException;
import java.time.LocalDateTime;

public class Follower {
    private Long id;
    private UserId followerId;
    private UserId followingId;
    private LocalDateTime createdAt;

    private Follower(UserId followerId, UserId followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.createdAt = LocalDateTime.now();
    }
    
    // Default constructor for JPA/reflection if needed, though this is a domain model
    protected Follower() {}

    public static Follower create(UserId followerId, UserId followingId) {
        if (followerId == null || followingId == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        if (followerId.equals(followingId)) {
            throw new BusinessException("A user cannot follow themselves");
        }
        return new Follower(followerId, followingId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserId getFollowerId() {
        return followerId;
    }

    public UserId getFollowingId() {
        return followingId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
