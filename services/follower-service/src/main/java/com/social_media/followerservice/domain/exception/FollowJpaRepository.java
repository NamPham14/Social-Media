package com.social_media.followerservice.domain.exception;

public class FollowJpaRepository extends RuntimeException {
    public FollowJpaRepository(String message) {
        super(message);
    }
}
