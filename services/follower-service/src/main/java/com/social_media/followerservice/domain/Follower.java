package com.social_media.followerservice.domain;

import com.social_media.followerservice.domain.exception.BusinessException;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "follow_relations")
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "follower_id", nullable = false))
    private UserId followerId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "following_id", nullable = false))
    private UserId followingId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Default constructor for JPA
    protected Follower() {}

    private Follower(UserId followerId, UserId followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.createdAt = LocalDateTime.now();
    }

    public static Follower create(UserId followerId, UserId followingId) {
        if (followerId == null || followingId == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        if (followerId.value().equals(followingId.value())) {
            throw new BusinessException("A user cannot follow themselves");
        }
        return new Follower(followerId, followingId);
    }

    public Long getId() {
        return id;
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
}
