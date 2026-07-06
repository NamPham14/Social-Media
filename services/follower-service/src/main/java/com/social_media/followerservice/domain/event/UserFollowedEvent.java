package com.social_media.followerservice.domain.event;

public record UserFollowedEvent(Long followerId, Long followingId) {}
