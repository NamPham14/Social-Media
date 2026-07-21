package com.social_media.postservice.domain.model.post.event;


import java.util.UUID;

public record PostCreatedEvent(UUID postId, String caption) {}
